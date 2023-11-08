## Mastery Task 3 Dagger Design Template

### Analyze `App.java`

Looking through `App.java` and your class diagram, identify:

1. Which classes should be returned from the Dagger component?
2. Which classes have constructors that should be annotated with `@Inject`?
   * All of the above classes that are considered root objects should be annotated with 
   `@Inject` because they are all classes written by us. Within each of those classes 
   right off of the bat, all of the `Dao` classes should be annotated with `@Inject` as 
   well because those were also written by us.
3. Which objects will we have to provide in a Provider method in a Module class?
   * The `DynamoDbMapper` class will need to be provided in the `Module` class because 
   it is an object that was not created/written by us.
   

   **Recall**: *Java annotations are ways to provide additional information
   to code that can be used during compilation or runtime, such as `@Override`,
   `@Test`, or `@Mock`. The action of adding an annotation is called "annotating".*

When identifying the classes, think about:
1. Which classes only **have** dependencies on other classes, i.e. the
   "root" classes that we interact with at the top of our service?
2. Which classes only **are** dependencies of other classes, but have
   no dependencies of their own?
3. Which classes both **are** dependencies of other classes and **have**
   dependencies on other classes?

**Remember that:**
1. "Root" classes should be provided using Dagger's `Component` interface,
   and should have their constructors annotated with `@Inject`.
2. All the root classes' dependencies should either:
   1. have constructors annotated with `@Inject`
   2. **or** be provided in Provider methods of Module classes that can
      be registered to Dagger's `Component` interface.
3. If there are relevant Singleton classes, Dagger's `Component` interface
   should also be annotated `@Singleton`.

### Capture Your Analysis

List the class(es) that `App.java` provides that are **not** dependencies of other 
classes, that is, no other classes in the project depend on these classes:

  * `CreatePlaylistActivity`
  * `GetPlaylistActivity`
  * `UpdatePlaylistActivity`
  * `AddSongToPlaylistActivity`
  * `GetPlaylistSongActivity`

List the class(es) that `App.java` provides that **are** dependencies of other classes:

   * `PlaylistDao`
   * `AlbumTrackDao`
   * `DynamoDBMapper`

List the class(es) that `App.java` creates that have constructors we must annotate with
`@Inject`:

   * `CreatePlaylistActivity`
   * `GetPlaylistActivity`
   * `UpdatePlaylistActivity`
   * `AddSongToPlaylistActivity`
   * `GetPlaylistSongActivity`
   * `PlaylistDao`
   * `AlbumTrackDao`

List the class(es) that `App.java` creates that we must provide in a Dagger module:

   * `DynamoDBMapper` 

List the class(es) that `App.java` creates as Singletons:

   * `DynamoDBMapper` 

### Pseudocode Dagger classes

Fill in the below annotations and method declarations.

We require that you name your component, `ServiceComponent`, and
your module, `DaoModule`, as indicated below. Use these names in
your implementation as well.

```
@Singleton
@Component(modules = {DaoModule.class})
public interface ServiceComponent {
    public provideCreatePlaylistActivity();

    public provideGetPlaylistActivity();

    public provideUpdatePlaylistActivity();

    public provideAddSongToPlaylistActivity();

    public provideGetPlaylistSongActivity();
}
```

```
@Module
public class DaoModule {

    @Provides
    @Singleton
    public DynamoDBMapper provideDynamoDbMapper() {
        // Implementation in milestone 2
    }
}
```
