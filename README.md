# unio-kt

## Introduction

Ordinary ViewModels might be implemented like this. But those implementation are not clear, for exmple which are states and which are dependencies and so on.

```kotlin
class CounterViewModel : ViewModel() {
    val count: Flow<String>
        get() = _count.map { it.toString() }

    val isCountDownEnabled: Flow<Boolean>
        get() = _isCountDownEnabled

    private val _count = MutableStateFlow(0)
    private val _isCountDownEnabled = MutableStateFlow(false)

    fun countUp() {
        _count.value = _count.value + 1
        _isCountDownEnabled.value = _count.value > 0
    }

    fun countDown() {
        _count.value = _count.value - 1
        _isCountDownEnabled.value = _count.value > 0
    }
}
```

## About Unio

Unio is kProperty based **Un**idirectional **I**nput / **O**utput framework that works with Flow.

- [Unio.Input](#unioinput)
- [Unio.Output](#uniooutput)
- [Unio.State](#uniostate)
- [Unio.Extra](#unioextra)
- [Unio](#unio)
- [UnioFactory](#uniofactory)
- [UnioViewModel](#unioviewmodel)

### Unio.Input

The rule of Input is having MutableSharedFlow properties that are defined internal (or public) scope.

```kotlin
class CounterUnioInput : Unio.Input {
    val countUp = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val countDown = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}
```

Properties of Input are defined internal (or public) scope.
But these can only access `InputProxy#getLambda` via kProperty if Input is wrapped with `InputProxy`.

```kotlin
val input: InputProxy<CounterUnioInput>

input.getLambda(CounterUnioInput::countUp).invoke()  // accesses `MutableSharedFlow#tryEmit`
```

### Unio.Output

The rule of Output is having MutableStateFlow properties that are defined internal (or public) scope.

```kotlin
class CounterUnioOutput(
    val count: Flow<String>,
    val isCountDownEnabled: MutableStateFlow<Boolean>,
) : Unio.Output
```

Properties of Output are defined internal (or public) scope.
But these can only access `Flow` (or StateFlow) via kProperty if Output is wrapped with `OutputProxy`.

```kotlin
val output: OutputProxy<CounterUnioOutput>

output.getFlow(CounterUnioOutput::count)
    .onEach { Log.d("UNIO_DEBUG", it.toString()) })

output.getFlow(CounterUnioOutput::isCountDownEnabled)
    .onEach { Log.d("UNIO_DEBUG", it.toString()) })
```

If a property is MutableStateFlow, be able to access value via kProperty.

```kotlin
output.getValue(CounterUnioOutput::isCountDownEnabled)
```

If a property is defined as `Computed`, be able to access computed value.

```kotlin
class Output: Unio.Output {
    val isEnabled: Computed<Bool>
}

var _isEnabled = false
let output = OutputProxy(Output(Computed<Boolean> { _isEnabled }))

output.getComputed(CounterUnioOutput::isEnabled) // false
_isEnabled = true
output.getComputed(CounterUnioOutput::isEnabled) // true
```

### Unio.State

The rule of State is having inner states of [Unio](#unio).

```kotlin
class State: Unio.State {
    val count = MutableStateFlow(0)
    val isCountDownEnabled = MutableStateFlow(false)
}
```

### Unio.Extra

The rule of Extra is having other dependencies of [Unio](#unio).

```swift
class Extra(val githubApi: GitHubhAPI): Unio.Extra
```

### Unio

The rule of Unio is generating [Unio.Output](#uniooutput) from Dependency<Input, State, Extra>.
It generates [Unio.Output](#uniooutput) to call `Unio#bind`.
It is called once when [Unio](#unio) is initialized.

```kotlin
class CounterUnio(
    input: CounterUnioInput,
    state: State,
    extra: Extra,
    viewModelScope: CoroutineScope,
) : Unio<
        CounterUnioInput,
        CounterUnioOutput,
        CounterUnio.Extra,
        CounterUnio.State
        >(
    input = input,
    extra = extra,
    state = state,
    viewModelScope = viewModelScope
)
```

Connect sequences and generate [Unio.Output](#uniooutput) in `Unio#bind` to use below properties and methods.

- `Dependency#state`
- `Dependency#extra`
- `Dependency#getFlow` ... Returns a flow that is property of [Unio.Input](#unioinput).
- `viewModelScope` ... It might be ViewModel lifecycle.

Here is a exmaple of implementation.

```kotlin
override fun bind(
    dependency: Dependency<CounterUnioInput, Extra, State>,
    viewModelScope: CoroutineScope
): CounterUnioOutput {
    val state = dependency.state
    val extra = dependency.extra

    listOf(
        dependency.getFlow(CounterUnioInput::countUp).map { 1 },
        dependency.getFlow(CounterUnioInput::countDown).map { -1 }
    )
        .merge()
        .map { state.count.value + it }
        .onStart { emit(extra.startValue) }
        .onEach {
            state.count.emit(it)
            state.isCountDownEnabled.emit(it > 0)
        }
        .launchIn(viewModelScope)

    return CounterUnioOutput(
        count = state.count.map { it.toString() },
        isCountDownEnabled = state.isCountDownEnabled,
    )
}
```

### UnioFactory

The rule of UnioFactory is generating [Unio](#unio).

```kotlin
class CounterUnioFactoryImpl : UnioFactory<CounterUnioInput, CounterUnioOutput> {
    override fun create(viewModelScope: CoroutineScope) = CounterUnio(
        input = CounterUnioInput(),
        state = CounterUnio.State(),
        extra = CounterUnio.Extra(5),
        viewModelScope = viewModelScope
    )
}
```

### UnioViewModel

UnioViewModel represents AAC ViewModel.
It has `val input: InputProxy<Input>` and `val output: OutputProxy<Output>`.
It automatically generates `val input: InputProxy<Input>` and `val output: OutputProxy<Output>` from instances of [Unio.Input](#unioinput), [Unio.State](#uniostate), [Unio.Extra](#unioextra) and [UnioFactory](#uniofactory).

Be able to define a subclass of UnioViewModel like this.

```kotlin
class CounterViewModel : UnioViewModel<CounterUnioInput, CounterUnioOutput>(CounterUnioFactoryImpl())
```

### Usage

This is example usage in an Activity.

```kotlin
class MainActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()
    private var scope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.count_up_button).setOnClickListener {
            viewModel.input.getLambda(CounterUnioInput::countUp).invoke()
        }

        val countDownButton = findViewById<Button>(R.id.count_down_button)
        countDownButton.setOnClickListener {
            viewModel.input.getLambda(CounterUnioInput::countDown).invoke()
        }

        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main).also { scope ->
            viewModel.output
                .getFlow(CounterUnioOutput::isCountDownEnabled)
                .onEach {
                    countDownButton.isEnabled = it
                }
                .launchIn(scope)

            val textView = findViewById<TextView>(R.id.textView)
            viewModel.output
                .getFlow(CounterUnioOutput::count)
                .onEach {
                    textView.text = it
                }
                .launchIn(scope)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope?.cancel()
        scope = null
    }
}
```

## Dagger Hilt Compatible

You can use unio-kt with Dagger Hilt.

```kotlin
@HiltViewModel
class CounterViewModel @Inject constructor(
    @CounterUnioFactory unioFactory: UnioFactory<CounterUnioInput, CounterUnioOutput>,
) : UnioViewModel<CounterUnioInput, CounterUnioOutput>(unioFactory)
```

```kotlin
class CounterUnioFactoryImpl @Inject constructor() : UnioFactory<CounterUnioInput, CounterUnioOutput> {
    override fun create(viewModelScope: CoroutineScope) = CounterUnio(
        input = CounterUnioInput(),
        state = CounterUnio.State(),
        extra = CounterUnio.Extra(5),
        viewModelScope = viewModelScope
    )
}
```

```kotlin
@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @CounterUnioFactory
    fun bindUnioFactory(
        unioFactory: CounterUnioFactoryImpl
    ): UnioFactory<CounterUnioInput, CounterUnioOutput>
}
```

## Setup

### Gradle

Add  `maven { url 'https://jitpack.io' }` to `settings.gradle`.

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" } // <--
    }
}
```

In your app `build.gradle`:

```
implementation 'com.github.marty-suzuki:unio-kt:0.1.0'
```

When import `unio-ki`, *Hyphone (marty-suzuki)* is wrong, **Underscore (marty_suzuki)** is correct.
(`import com.github.marty_suzuki.unio.*`)

## Related
- [Unio](https://github.com/cats-oss/Unio) for iOS.

## License

unio-kt is released under the Apache License 2.0.