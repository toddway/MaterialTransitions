# Notes


## Shared element transitions with Fragment transactions
- animating a shared element appears to only work when using .replace() to replace frag1 with frag2
- Unlike Activity transitions, the view overlay is not used for fragment transitions so shared elements
 may animate behind other views (especially when reversing a transition).  setElevation() can help.
- if transitionName is set at runtime (with java - not xml) it may not survive all lifecycle events.
E.g. when returning to a fragment from popBackStack()
- On Activity transitions the second activity's elements are animated when the transition is played forward AND
when reversed.  For Fragment transitions, the second activity's elements are animated when played forward but
the first activity's elements are animated when reversed.
-
http://www.androiddesignpatterns.com/2015/01/activity-fragment-shared-element-transitions-in-depth-part3a.html
https://android.googlesource.com/platform/frameworks/base/+/a712e8cc2f16ac32ee5f1bbf5b962969f2f3451e/core/java/android/app/EnterTransitionCoordinator.java
http://stackoverflow.com/questions/28386397/shared-element-transitions-between-views-not-activities-or-fragments
https://github.com/saulmm/Android-Material-Examples
http://stackoverflow.com/questions/29145031/shared-element-transition-works-with-fragmenttransaction-replace-but-doesnt-w