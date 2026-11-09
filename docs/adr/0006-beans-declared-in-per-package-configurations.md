# Beans are declared in one @Configuration per package

## Status

accepted

## Context

`jelatyna-backend` got its beans two ways. About two dozen services, adapters and helpers
carried `@Service` or `@Component` and were found by `@SpringBootApplication`'s component
scan; the rest were built by `@Bean` methods in a handful of `@Configuration` classes.
Nothing showed a feature's wiring in one place: to learn what a package put into the
context, a reader had to look for stereotypes in every class. The scanned classes were
public by habit, though most were used only inside their package, and
`@ConfigurationPropertiesScan` registered every properties class, so nothing said which
bean reads which settings.

## Decision

**Every bean is built by a `@Bean` method in a `@Configuration` in its own package.** A bean
class carries no stereotype; its configuration constructs it and passes every dependency
through the `@Bean` method's parameters, so constructor injection is the only injection.

- **One configuration per package that owns beans**, named by its package:
  `<Feature>Configuration` in a `domain` package (`AgendaConfiguration`),
  `<Feature>WebConfiguration` in a `web` package (`UserWebConfiguration`),
  `<System>Configuration` in an adapter package (`MandrillConfiguration`,
  `ListmonkConfiguration`) and `<Subpackage>Configuration` in `platform`
  (`WebConfiguration`, `StorageConfiguration`, `PersistenceConfiguration`). `demo` has one
  per profile, with the profile on the class: `FakeDbConfiguration` (`fake-db`) and
  `FakeSecurityConfiguration` (`fake-security`). Existing configurations were renamed or
  merged to fit; `ChatConfiguration` and `ArchiveConfiguration` already did.
- **Bean names stay as the scan made them.** A `@Bean` method is named after its class,
  decapitalised (`AgendaService agendaService(...)`). `@PreAuthorize` expressions depend
  on two of the names, `@security` and `@presentationSecurity`. The scan left a class name
  that starts with two capitals whole (`OAuthUserService`), so those `@Bean` methods give
  the name explicitly (`@Bean("OAuthUserService")`).
- **Package-private by default.** A bean class that nothing outside its package uses is
  package-private (`MandrillMailSender`, `JwtAuthenticationFilter`, `TalkMover`, ...):
  only its configuration constructs it, and other packages see the port it implements.
- **Each configuration enables the properties its beans read** with
  `@EnableConfigurationProperties`; `@ConfigurationPropertiesScan` is gone. Two
  configurations may enable the same class.
- **Controllers stay scanned.** Spring MVC finds `@RestController`/`@Controller` classes by
  their stereotype and nothing injects them, so a `@Bean` per controller would add a method
  each and tell a reader nothing. `@SpringBootApplication` keeps its default scan, which
  now finds only controllers and configurations.
- **Enforcement.** `ArchitectureTest.beansAreDeclaredInConfigurations`: no class is
  annotated, directly or through a meta-annotation, with `@Component` (which covers
  `@Service` and `@Repository`) unless it is a `@Configuration`, a controller or a
  `@ConfigurationProperties` class.

**Next step: per-feature entry points.** With each package declaring its beans, a feature
can expose one configuration that `@Import`s its packages' configurations, and the
application or a test slice can import features instead of scanning for them. That is a
separate change.

## Rejected alternatives

- **Keep the component scan for services.** Less code, but a feature's wiring stays spread
  over its classes' annotations, and a stereotype in the wrong package goes unnoticed.
- **One configuration per feature** across its `domain`, `web` and adapter packages: it
  would sit outside most of the classes it builds, so they would have to stay public.
- **`@Bean` methods for controllers too:** a method per controller, for no gain.
- **Turning the scan off now:** it needs the per-feature entry points first.

## Consequences

- A new service needs a `@Bean` method next to it. Forgetting it fails the context at
  startup; adding `@Service` instead fails `ArchitectureTest`.
- Class-level annotations that only the scan read (`@Profile`, `@DependsOn`, a bean name)
  go on the `@Bean` method or on a profile-specific configuration.
- The post-processors treat `@Bean`-built beans like scanned ones: `@Transactional`,
  `@EventListener`, `@PostConstruct` and `@Scheduled` keep working. A package-private
  `@Transactional` class is proxied by subclassing, so it must not be final.
