
# Chat Feed Android Challenge
This repository contains my solution to an Android Challenge requested during an application process to a specific company.

## Challenge Description
The goal of the challenge was to create a complete Android application resembling a chat feed, which automatically fetches information from an API. Additionally, the chat responds to several commands which change the polling's behavior.

**To keep the authenticity of the challenge for others, I will not be describing the exact specifications or requirements. The goal of this README is to give a high-level description of my solution.**

## Solution
The solution adheres to all specification points, and is encompassed by a complete architectural showcase of my knowledge. I used the latest Android tech stack and I used MVVM as my architectural pattern of my choice. 

The skeleton of the project is extensive, as I wanted to showcase my knowledge and act as this project would've been the beginning of a larger project, or part of something bigger. The code was designed to support scalability, hence the inclusion of items such as the `Navigation Components library`, or adding possibilities of interceptors into the `OkHttp` instantiation. These might not have been necessary to the function of the project, but they were intended to showcase Android knowledge.

## Dependencies
**Language**: Kotlin

**Min. SDK version**: API 24 (Android 7)

**Architecture pattern**: MVVM

**Dependency injection**: Koin

**Concurrency**: Coroutines + Flow

**Build scripts**: [Kotlin DSL instead of Groovy.](https://android-developers.googleblog.com/2023/04/kotlin-dsl-is-now-default-for-new-gradle-builds.html) 


Dependencies are as follows: 
|Name            |Usage													|Mandatory?											|
|----------------|-------------------------------|-----------------------------|
|`AppCompat`|Access of newer APIs on older platforms.|Yes|
|`KTX Core`|Kotlin, Jetpack and Android idiomatic extensions.|Yes           |
|`ConstraintLayout`|View library.|Yes|
|`Android Material Components`|Material Components library.|Yes|
|`Navigation Components`|Jetpack navigation library.|Yes|
|`Lifecycle Extensions`|Extensions for Lifecycle-aware components.|Yes|
|`LiveData`|Observable, data holder class.|Yes|
|`Koin `|Dependency injection framework.|Yes|
|`RecyclerView`|View library.|Yes|
|`Retrofit`|Type-safe HTTP client.|Yes|
|`OkHttp 3`|Underlying HTTP client interface.|Yes|
|`Moshi`|JSON library.|Yes|
|`Coroutines`|Concurrency library.|Yes|
|`Timber`|Modern logging utility.|Yes|

## Code quality & style
Code quality and style is handled by `Detekt`. The currently active rule set can be found in the `app` module's root folder, under the name `detekt-config`. The rule set is strict, has a 10-warning limit, after which, it'll fail a build. 

Additionally, there is a project-level code-style file with custom values *(such as a 150 character line limit and visual guide)* which should automatically be read by Android Studio upon launch.

## Specification unknowns
The specification requires the following: 
> Every 5 seconds a new feed is fetched from the API and should be added to the displayed feeds list.

It is unclear what that behaviour entails. There are two options:

**Option 1**: The 5-second "ticker" should run independently of the polling's result, with no regard to its network or processing delay.

**Option 2**: The 5-second timer should only start once a response has been processed. 

In the case of the first option, there can be cases with slow networks or long processing delays where there are far less seconds than 5 between each poll.  

In the case of the second option, due to processing and network delays, the actual time between polls is always going to be more than 5 seconds.

I've personally implemented the first option as it was more of a challenge than just waiting 5 second after each network response.

## Technical debt
Unfortunately, due to limited time and scope of the challenge, some technical debt was left in the project. They can all be solved, and the purpose of this segment is to showcase that, and point them out.

> NetworkResponseAdapterFactory.kt

It's not the end of the world, but this `CallAdapter` is using multiple `return` statements, which is unfortunate, and breaks a code-style rule in `Detekt.` It would need to be refactored  to only use one. Something like initiating the return value first, as null, and only overwriting it in successful cases could work. 

> ErrorObserver.kt

Due to the scope of the project, I did not make a specific `ErrorView`, but this class would be the foundation of that. Relevant actors implement the `ErrorObserver` interface, as seen in the `ChatFragment`.

> BaseViewModel.kt

Default error messages should definitely use String resources instead of burned-in values, to support localization.

> ChatFragment.kt

The way that automatic scrolling to the bottom of the list is handled right now is not the greatest. Ideally, I'd develop a custom LinearLayoutManager, which would scroll to the bottom after every layout change. 

> Screen panning

Currently, `adjustResize` is used at a `windowSoftInputMode` panning option, but other options, or a custom implementation might be better. When the software keyboard appears, the list is currently not scrolled to the new bottom with `adjustResize` for example. 

## Known issues

> Issue where, in cases of long network or processing delays, the chat could display items even though a new command has been issued and displayed, which would've prevented that item of displaying. 

This is an unfortunate bug with the way the "ticker" works currently. As commented in the `ChatPollingEngine`, the timer runs independently of the polling's result, with no regard to its network or processing delay. Imagine the following case:

-  A new network request is sent, as the `ChatPollingEngine` is currently fetching items.
- There is an average 900ms network response time
- A new `STOP` command arrives during the 900ms time-frame. 
- The expected behaviour would be that the application disregards the result of the network request.
- However, the observed behaviour is that the `STOP` command is displayed, as expected, but the network response that was already started, will display after it finishes. 

This behaviour leaves that time-frame when we are waiting for network responses, extremely dangerous, as they're asynchronous functions. 

A potential resolution would be to make sure that the discharging of single items (and incrementing the page counter) is handled by the `ChatPollingEngine` internally. In that case, after a successful network response, the `ViewModel` would just report back to the `Engine` with the result item, and the `Engine` would separately tell the `ViewModel` to discharge the item if it deems it appropriate, while incrementing the page counter.

## Credits
This is work is my of my own creation. The architecture and project skeleton itself is my own, as I've been using and updating it as the years went by. 

A larger chunk of code that did not originate from me was the custom `CallAdapterFactory` for Coroutines and Retrofit. I've created that when I transitioned from RXJava to Coroutines. It has been improved upon since then, however. 

Generally speaking, there was some extensive Googling involved, and also asking ChatGPT question like "How can I destroy or cancel a Flow in a way that it doesn't leak?".
