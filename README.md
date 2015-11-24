# Android Material Transitions

<img src="https://raw.githubusercontent.com/toddway/MaterialTransitions/master/img/activity-transitions.gif" style="width:210px;border:1px solid #eeeeee"/>

[This Android project](https://github.com/toddway/MaterialTransitions) samples some Material Design-ish transitions for list items and floating action buttons.  It uses the
[the shared element concept introduced in Android 5.0](https://developer.android.com/training/material/animations.html).  I tried to pull it off with pure fragment transitions and ran into a few stags (see below)
so my current solution uses an activity transition for each step.

[[MORE]]

## Activity Transition tricks:

- Generate a background bitmap immediately before the transition and pass it to the called activity
- Suppress the view overaly (used by default for activity transitions) to keep shared elements behind the toolbar & system bars
- Fall back to fade and scale activity transitions when < 5.0

## Fragment Transition issues:

- animating a shared element appears to only work when using .replace() - not .add()
- Unlike Activity transitions, the view overlay is not used for fragment transitions so shared elements
  might animate behind other views (especially when reversing a transition).  setElevation() helps some of the time.
- if transitionName is set at runtime (with java - not xml) it may not survive all lifecycle events.
  E.g. when returning to a fragment from popBackStack()
- On Activity transitions the second activity's elements are animated when the transition is played forward AND
when reversed.  For Fragment transitions, the second activity's elements are animated when played forward but
the first activity's elements are animated when reversed.

## More Help
- http://www.androiddesignpatterns.com/2015/01/activity-fragment-shared-element-transitions-in-depth-part3a.html
- [https://android.googlesource.com/platform/frameworks/base/...](https://android.googlesource.com/platform/frameworks/base/+/a712e8cc2f16ac32ee5f1bbf5b962969f2f3451e/core/java/android/app/EnterTransitionCoordinator.java)
- http://stackoverflow.com/questions/28386397/shared-element-transitions-between-views-not-activities-or-fragments
- https://github.com/saulmm/Android-Material-Examples
- http://stackoverflow.com/questions/29145031/shared-element-transition-works-with-fragmenttransaction-replace-but-doesnt-w

License
-------

    Copyright 2015 Todd Way

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
