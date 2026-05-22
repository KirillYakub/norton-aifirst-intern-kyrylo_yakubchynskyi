# Security Health Dashboard

Option A: Build a "Security Health Dashboard" prototype inspired by the Smart Scan experience from Norton 360.

# App Overview:
* Home screen with "Scan Now" button and pull-to-refresh action
* Smart Scan screen that displays overall security score with animated circular progress indicator and category checks for: OS version, app threats, Wi-Fi safety, password strength.
* Horizontal pager with three sections (Possible Risks, Recommendations, Protection Upgrades)
* Proper back-button confirmation dialog during active scan
* Realistic mock data simulation with Flow and error state handlers

App also supports next Norton 360 Smart Scan UI/UX patterns:
* Scan progress reveals category-by-category instead of instantly displaying a result.
* Recommendation-Based UX: Instead of only reporting problems, app immediately suggests actions
* Usage of color-coded security states makes scan results easy to understand immediately
* Contains reusable common UI components (RecommendationCard, SecurityCheckItem, PrimaryActionButton)

# Tech Stack:
* Android
* Kotlin
* Jetpack Compose
* MVI as Architecture Design Pattern
* Kotlin StateFlow, Channel, Coroutines, Flows
* Koin DI
* Navigation 3
* JUnit

# Testing

The project contains 6 unit tests covering:

* ViewModel state transitions
* Scan completion logic
* Error handling
* Score mapping
* Back button behavior
* Scan cancellation

# Setup Instruction
* Download .apk release file directly from Github on your Android Device
* Or use next command in your git/terminal: git clone "https://github.com/KirillYakub/norton-aifirst-intern-kyrylo_yakubchynskyi.git" - fully download Android Studio project

# Architecture
The project uses a clean single-module architecture organized by feature.

```text
com.norton.securitydashboard
├── data
├── di
├── domain
│   ├── model
│   └── repository
├── feature
│   ├── home
│   └── scan
│       └── components
├── navigation
└── ui
    ├── components
    └── theme
```

# AI Interaction Log
Before starting to work with Claude Code directly I used a combination with ChatGpt + Grok to create docs/spec.md file which contains necessary requirements for this internship project. After that I have started with next prompt:

### Prompt 1 - Architecture Design

```text
You are a Senior Android Engineer at Norton (Gen).
I am building the Security Health Dashboard prototype.
The complete product layout, behaviors, colors, and requirements are written in docs/spec.md. Read the entire file first.
Lets begin with Phase 1 - Architecture and Domain Models.

Please provide:
1. A clean single-module package structure
2. Plain Kotlin domain models
3. A single immutable UI State class
```

### Result

Claude generated:
* initial package structure
* domain models
* scan state models
* feature-based architecture plan which you can search above
* plan for generating project files, broken down into phases

### My Refinements:
* simplified package structure
* removed unnecessary complexity
* adjusted architecture for internship scope

However before I made a mistake with project package naming, so lets fix it:
## Prompt 2 — Package Cleanup and Refactor

```text
Change the package name to: applicationId = "com.norton.securitydashboard"
Delete the old package structure.
Move MainActivity and theme files into the new package.
Fix all imports and package declarations.
```

### Result

Claude:
* renamed package structure
* updated Gradle configuration
* cleaned imports

### My Refinements:
* verified no leftover packages remained
* manually checked all imports/build issues

After that we needed to create repository abstraction and mock scan simulation.
## Prompt 3 — Repository Layer

```text
Proceed to Phase 2 - Repository Layer

Create:                                                                                                                                                               
1. ScanRepository interface in domain/repository/                                                                                                                 
2. ScanRepositoryImpl in data/ package                                                                                                                                   

Use Koin for DI. Simulate a realistic 3–5 second scan using Kotlin Flow with sequential category updates and occasional errors.  
```

### Result

Claude generated:
* repository abstraction
* mock scan simulation
* Flow-based updates
* delayed scan behavior

Now we could generate screens, ViewModels, reusable components, and state handling.
## Prompt 4 — UI & ViewModels

```text
Now create everything you marked for Phase 3, including ScanViewModel.kt, and any other files you planned for this phase (HomeScreen, ScanScreen, components, etc.)                                                           
Use Koin to inject the repository.                                                                                                                       
Follow your own plan and the rules in docs/spec.md.                                                                                                                   
```

### Result

Claude generated:
* HomeScreen
* SmartScanScreen
* ScanViewModel
* HomeViewModel
* reusable UI components
* navigation handling

Also Claude made next mistakes:
* Generated unnecessary /core/ui package with ObserveAsEvents fun
* Duplicated the DI packages
* Put some common components into /feature/scan/components

### My Refinements
* moved shared components into ui/components
* moved ObserveAsEvents into ui
* told Claude to fix DI dublication with next prompt:

## Prompt 5 — Fix DI duplication

```text
We have duplicated DI modules.                                                                                                         
                                                                                                                                                                        
Clean up the DI structure:                                                                                                                                     
- Keep only one clean Koin module
- Delete the redundant DI module/folder                                                                                                                               
- Make sure ScanRepositoryImpl is still correctly injected with Koin                                                                                                
```

### Result

Claude generated:
* One common AppModule
* Removed packages dublication

Now I generated tests:
## Prompt 6 — Testing

```text                                                                                                                                     
Now create at least 3 unit tests as required in the assignment.                                                                                        
Cover ViewModel and data model logic (state transitions, score calculation, cancellation behavior are good examples).
```

### Result

Claude generated:
* ScanViewModelTest
* coroutine-based tests
* fake repositories
* event/cancellation tests

### My Refinements
* renamed several functions for clarity
* verified coroutine behavior manually

At the end Claude performed a final engineering review.
## Prompt 7 — Final AI Review

```text
Final review.                                                                                                                                                         
Read docs/spec.md and review the entire current project.                                                                                                              
Act as a Senior Android Engineer.   
```

### Result

Claude identified:
* pull-to-refresh navigation mismatch
* naming cleanup opportunities
* scrollability issue
* Compose stability optimization

### My Refinements
* fixed pull-to-refresh navigation
* renamed repository implementation
* added @Stable annotation
* manually reviewed final package structure

## Claude Final Verdict: 
Ready for internship submission with one fix: wire pull-to-refresh to navigation on the Home screen — it's the only genuine spec deviation. The rest is polish. 
Architecture, state management, and test coverage are all production-quality for a prototype of this scope. Strong work.

# AI Workflow Reflection:

This project taught me that AI-assisted engineering works best when the developer acts as the technical lead rather than treating the AI as a full autonomous generator.
When you work with AI as with Junior Developer and write him structured documentation, it significantly improves AI output quality. 
I created a dedicated spec.md file that acted as the single source of truth for the project and guided AI to advance code generation and bug fixing.

In real work you will not have such a detailed description as for this internship project, but if you have good Figma designs or understand the project requirements, creation of a quality feature is only a question of time with necessary .md files and step-by-step prompts.
It is also important to know how to write this manually, understand how Android works under the hood, know Coroutines, Flows, etc. But if you are a developer who knows Android deeply and can master AI tools, with time you will become irreplaceable for companies.

# Demo Video:

Link: https://drive.google.com/file/d/1jvt4ZNMjvZEmmEE-1yxtospeX01zhSIh/view?usp=sharing

# App Screenshots:

## Home Screen
<img src="docs/screens/home_screenshot.png" width="300"/>

## Smart Scan
<img src="docs/screens/scan_process_screenshot.png" width="300"/>

## Scan Fail
<img src="docs/screens/scan_failed_screenshot.png" width="300"/>

## Scan Results
<img src="docs/screens/scan_result_screenshot.png" width="300"/>

## Back Dialog
<img src="docs/screens/go_back_dialog_screenshot.png" width="300"/>
