/*******************************************************************************
* Copyright (c) 2023, 2025 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Ankush Sharma - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.utils.PositionUtils;
import org.eclipse.lsp4jakarta.jdt.internal.DiagnosticUtils;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;

/**
 * Persistence diagnostic participant that manages the use
 * of @MapKeyClass, @MapKey, and @MapKeyJoinColumn annotations.
 */
public class PersistenceMapKeyDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) throws CoreException {
        String uri = context.getUri();
        IJDTUtils utils = JDTUtilsLSImpl.getInstance();
        ICompilationUnit unit = utils.resolveCompilationUnit(uri);
        List<Diagnostic> diagnostics = new ArrayList<>();

        if (unit == null) {
            return diagnostics;
        }

        IType[] alltypes = unit.getAllTypes();
        IMethod[] methods;
        IField[] fields;

        for (IType type : alltypes) {
            methods = type.getMethods();
            collectMemberDiagnostics(methods, type, unit, diagnostics, context);
            fields = type.getFields();
            collectMemberDiagnostics(fields, type, unit, diagnostics, context);
        }

        return diagnostics;
    }

    private void validateMapKeyJoinColumnAnnotations(JavaDiagnosticsContext context, String uri,
                                                     List<IAnnotation> annotations,
                                                     IMember element,
                                                     ICompilationUnit unit, List<Diagnostic> diagnostics) throws CoreException {

        annotations.forEach(annotation -> {
            boolean allNamesSpecified, allReferencedColumnNameSpecified;
            try {
                Range range = null;
                String message = null;
                ErrorCode errorCode = null;
                if (element instanceof IMethod) {
                    range = PositionUtils.toNameRange((IMethod) element, context.getUtils());
                    errorCode = ErrorCode.InvalidMethodWithMultipleMPJCAnnotations;
                    message = Messages.getMessage("MultipleMapKeyJoinColumnMethod");
                } else {
                    range = PositionUtils.toNameRange((IField) element, context.getUtils());
                    errorCode = ErrorCode.InvalidFieldWithMultipleMPJCAnnotations;
                    message = Messages.getMessage("MultipleMapKeyJoinColumnField");
                }

                List<IMemberValuePair> memberValues = Arrays.asList(annotation.getMemberValuePairs());
                allNamesSpecified = memberValues.stream().anyMatch((mv) -> mv.getMemberName().equals(Constants.NAME));
                allReferencedColumnNameSpecified = memberValues.stream().anyMatch((mv) -> mv.getMemberName().equals(Constants.REFERENCEDCOLUMNNAME));
                if (!allNamesSpecified || !allReferencedColumnNameSpecified) {
                    diagnostics.add(context.createDiagnostic(uri,
                                                             message, range,
                                                             Constants.DIAGNOSTIC_SOURCE, null,
                                                             errorCode, DiagnosticSeverity.Error));
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Error while retrieving member values of @MapKeyJoinColumn Annotation",
                                               e);
            }
        });
    }

    private void collectMemberDiagnostics(IMember[] members, IType type, ICompilationUnit unit,
                                          List<Diagnostic> diagnostics, JavaDiagnosticsContext context) throws CoreException {

        List<IAnnotation> mapKeyJoinCols = null;
        boolean hasMapKeyAnnotation = false;
        boolean hasMapKeyClassAnnotation = false;
        IAnnotation[] allAnnotations = null;

        // Go through each method/field to ensure they do not have both MapKey and MapKeyColumn Annotations
        for (IMember member : members) {
            mapKeyJoinCols = new ArrayList<IAnnotation>();
            hasMapKeyAnnotation = false;
            hasMapKeyClassAnnotation = false;
            allAnnotations = null;

            if (member instanceof IMethod) {
                allAnnotations = ((IMethod) member).getAnnotations();
            } else if (member instanceof IField) {
                allAnnotations = ((IField) member).getAnnotations();
            }

            for (IAnnotation annotation : allAnnotations) {
                String matchedAnnotation = DiagnosticUtils.getMatchedJavaElementName(type, annotation.getElementName(),
                                                                                     Constants.SET_OF_PERSISTENCE_ANNOTATIONS);
                if (matchedAnnotation != null) {
                    if (Constants.MAPKEY.equals(matchedAnnotation))
                        hasMapKeyAnnotation = true;
                    else if (Constants.MAPKEYCLASS.equals(matchedAnnotation))
                        hasMapKeyClassAnnotation = true;
                    else if (Constants.MAPKEYJOINCOLUMN.equals(matchedAnnotation)) {
                        mapKeyJoinCols.add(annotation);
                    }
                }
            }

            if (hasMapKeyAnnotation && hasMapKeyClassAnnotation) {
                //A single method/field cannot be annotated with both @MapKey and @MapKeyClass
                //Specification References:
                //https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/mapkey
                //https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/mapkeyclass
                Range range = null;
                String messageKey = null;
                ErrorCode errorCode = null;
                if (member instanceof IMethod) {
                    range = PositionUtils.toNameRange((IMethod) member, context.getUtils());
                    messageKey = "MapKeyAnnotationsNotOnSameMethod";
                    errorCode = ErrorCode.InvalidMapKeyAnnotationsOnSameMethod;
                } else if (member instanceof IField) {
                    range = PositionUtils.toNameRange((IField) member, context.getUtils());
                    messageKey = "MapKeyAnnotationsNotOnSameField";
                    errorCode = ErrorCode.InvalidMapKeyAnnotationsOnSameField;
                }
                diagnostics.add(context.createDiagnostic(context.getUri(), Messages.getMessage(messageKey), range,
                                                         Constants.DIAGNOSTIC_SOURCE, null, errorCode, DiagnosticSeverity.Error));
            }

            // If we have multiple MapKeyJoinColumn annotations on a single method/field
            // we must ensure each has a name and referencedColumnName
            if (mapKeyJoinCols.size() > 1) {
                validateMapKeyJoinColumnAnnotations(context, context.getUri(), mapKeyJoinCols, member, unit,
                                                    diagnostics);
            }
        }
    }
}
