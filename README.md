# Android Material Transitions

![Activity Transitions](img/activity-transitions.gif)

This project samples some material transition animations for Android.  The approach is based on
[shared element transitions introduced in Android 5.0](https://developer.android.com/training/material/animations.html)
I tried to pull it off with pure fragment transitions and ran into a few stags (see below)
so the current solution uses activity transitions.

## Primary tricks for the Activity Transition implementation:

- Generating a background bitmap immediately before a transition to be used in the called activity
- Suppressing the "view overlay" to keep shared elements behind the toolbar
- Fall back to fade and scale activity transitions when < 5.0

## Issues encountered in a Fragment Transition implementation:

- animating a shared element appears to only work when using .replace() - not .add()
- Unlike Activity transitions, the view overlay is not used for fragment transitions so shared elements
  may animate behind other views (especially when reversing a transition).  setElevation() helps some of the time.
- if transitionName is set at runtime (with java - not xml) it may not survive all lifecycle events.
  E.g. when returning to a fragment from popBackStack()
- On Activity transitions the called activity's elements are animated when the transition is played forward AND
when reversed.  For Fragment transitions, the second activity's elements are animated when played forward but
the first activity's elements are animated when reversed.

## Resources
- http://www.androiddesignpatterns.com/2015/01/activity-fragment-shared-element-transitions-in-depth-part3a.html
- https://android.googlesource.com/platform/frameworks/base/+/a712e8cc2f16ac32ee5f1bbf5b962969f2f3451e/core/java/android/app/EnterTransitionCoordinator.java
- http://stackoverflow.com/questions/28386397/shared-element-transitions-between-views-not-activities-or-fragments
- https://github.com/saulmm/Android-Material-Examples
- http://stackoverflow.com/questions/29145031/shared-element-transition-works-with-fragmenttransaction-replace-but-doesnt-w