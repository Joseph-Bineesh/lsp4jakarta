## Version 0.2.6 of Eclipse LSP4Jakarta

### New Features

#### JSON-B (JSON Binding)
- **Diagnostics and Quick Fixes for @JsonbTransient Annotation**
  - Diagnostic when @JsonbTransient is used with other JSON Binding annotations on getters (#644)
  - Diagnostic when @JsonbTransient is used with other JSON Binding annotations on setters (#645)
  - Quick fixes to remove @JsonbTransient or remove conflicting annotations (#647, #648)
  
- **Constructor and Modifier Validations**
  - Diagnostic for missing public/protected no-argument constructor in deserialization classes (#643)
  - Quick fix to add public no-argument constructor (#649)
  - Diagnostic for non-static inner classes used in JSON-B (#792)
  - Diagnostic for invalid method modifiers (must be public or protected) (#839)

- **Property Name Validation**
  - Diagnostics for unique JSON property names (#291)

#### Dependency Injection (DI)
- **Scope Annotations**
  - Diagnostic for scope annotations with invalid attributes (#655)
  - Quick fix to remove invalid attributes from scope annotations (#841)
  - Code snippet for @Scope annotation (#816)

- **Injectable Methods and Qualifiers**
  - Diagnostic for injectable methods declaring their own type parameters (#654)
  - Diagnostic for multiple qualifiers on inject fields/parameters (#758)
  - Code snippet for @Qualifier annotation (#759)

#### Interceptors
- **Interceptor Validations**
  - Diagnostic for abstract interceptor classes or missing public no-arg constructor (#656)
  - Diagnostic for interceptor methods not calling InvocationContext.proceed() (#658)
  - Diagnostic for interceptors declaring observer methods (#706, #854)
  - Code snippet for @Interceptor annotation (#700)

#### CDI (Contexts and Dependency Injection)
- **Observer Methods**
  - Diagnostic for multiple @Observes/@ObservesAsync parameters (#828, #829, #834)
  - Diagnostic for mutually exclusive @Observes and @ObservesAsync annotations (#617)
  - Diagnostic for @Dependent scope beans with conditional observer methods (#707, #842)

- **Session Bean Scopes**
  - Diagnostic for stateless session beans (must use @Dependent) (#709, #835)
  - Diagnostic for singleton session beans (must use @ApplicationScoped or @Dependent) (#710, #837)
  - Diagnostic for generic session beans (must use @Dependent) (#711)

#### Jakarta Persistence (JPA)
- **Entity Validations**
  - Diagnostic for invalid @Id field types (#724, #776)
  - Quick fix for invalid @Id field types (#814)
  - Diagnostic for multiple @Version fields in one entity (#733)
  - Diagnostic for final entity classes/methods/variables (#775)
  - Diagnostic for missing primary key in entity classes (#831)
  - Diagnostic for JPA named annotations on non-@Entity classes (#767)

- **Code Snippets**
  - Autocomplete snippet for @NamedQuery annotation (#730)
  - Autocomplete snippet for @NamedEntityGraph annotation (#729)
  - Snippet for EntityListener (#680, #809)

#### Jakarta Annotations
- **@Generated Annotation**
  - Diagnostic to enforce value element contains code generator name (#653, #843)

- **@Resource Annotation**
  - Diagnostic for JavaBeans setter method convention violations (#661)
  - Diagnostic for type compatibility issues (#662, #799)
  - Quick fixes for type compatibility (#803, #805)

- **@Priority Annotation**
  - Diagnostic to enforce non-negative values (#663, #808)

- **@PostConstruct/@PreDestroy**
  - Improved diagnostic support for checked exception conditions (#193)
  - Quick fix support for checked exception condition (#605)

- **Code Snippets**
  - Snippet for @DataSourceDefinition (#666)

- **Default Attribute Values**
  - Default values for annotation attributes determined by actual type (#611, #812)

#### Jakarta Servlet
- **Diagnostics**
  - Diagnostic for @DeclareRoles on non-servlet classes (#622, #822)

- **Code Snippets**
  - Snippet for @WebListener annotation (#623)
  - Snippet for @MultipartConfig (#626, #774)
  - Snippet for @ServletSecurity annotation (#628)

#### Jakarta WebSocket
- **Endpoint Validations**
  - Diagnostic for missing public no-arg constructor in @ServerEndpoint and @ClientEndpoint classes (#633, #795)
  - Diagnostic for duplicate onOpen, onClose, onError methods (#630, #800)
  - WebSocket prefix for Web Socket snippets (#732, #793)

#### Jakarta Bean Validation
- **Constraint Validations**
  - Diagnostics for constraints on method parameters (#618, #791)
  - Diagnostics for constraint annotations on setters/constructors (#610, #624, #804)
  - Diagnostics for conflicting constraint parameters (#620, #823)
  - Improved handling to show all valid constraint annotation diagnostics at once (#523)
  - Custom @Constraint annotation snippet (#629)
  - Diagnostics for Size and NotEmpty annotations (#63)

#### Jakarta Faces
- **Code Snippets**
  - Snippet for @FacesBehavior (#777)

#### Jakarta EJB
- **Code Snippets**
  - Snippet for Message Driven Bean (MDB) (#636, #785)
  - Snippet for EJB TimerService (#635)

#### Jakarta RESTful Web Services
- **Code Snippets**
  - Snippet for RestClient (#627)

#### Jakarta JSON-P (JSON Processing)
- **Validations**
  - Diagnostic for JsonObjectBuilder null key (#639)
  - Diagnostic for invalid JsonArrayBuilder add method (#640)

### Improvements & Fixes

- **Build and Infrastructure**
  - Introduced target platform module for lsp4jakarta (#821, #827)
  - Target platform documentation update (#833)
  - Maven version sync in Jenkins (#862)
  - Code formatting issues fixed (#864)

- **Code Quality**
  - Fixed file format issues (#798)
  - Automated data sync between jakarta-sample and demo-servlet-no-diagnostics (#810)

### Contributors

Thank you to all contributors who helped with this release:
- @Joseph-Bineesh
- @Rejoice472
- @aditrada
- @archana-1924
- @mrglavas
- @rezaakv
- @sajeerzeji
- @tiganov
- @turkeylurkey
- @venmanyarun
- @yeekangc

---

See the [commit log](https://github.com/eclipse-lsp4jakarta/lsp4jakarta/compare/0.2.5...HEAD) for the full set of changes.

**Note:** This release addresses 64 issues and includes 26 additional merged pull requests, with contributions from 11 developers.

The Eclipse LSP4Jakarta 0.2.6 early release will be available on the [Eclipse Repository](https://repo.eclipse.org/content/repositories/lsp4jakarta-releases/) and [Eclipse Download](https://download.eclipse.org/lsp4jakarta/releases/) site.