* Unmerged path app/src/main/res/layout/activity_main.xml
[1mdiff --git a/app/src/main/res/layout/activity_main.xml b/app/src/main/res/layout/activity_main.xml[m
[1mindex 49fae37..d8f8a62 100644[m
[1m--- a/app/src/main/res/layout/activity_main.xml[m
[1m+++ b/app/src/main/res/layout/activity_main.xml[m
[36m@@ -5,6 +5,7 @@[m
     android:id="@+id/main"[m
     android:layout_width="match_parent"[m
     android:layout_height="match_parent"[m
[32m+[m[32m<<<<<<< HEAD[m
     android:background="@color/background_color"[m
     tools:context=".MainActivity">[m
 [m
[36m@@ -173,11 +174,20 @@[m
         android:layout_width="wrap_content"[m
         android:layout_height="wrap_content"[m
         android:visibility="gone"[m
[32m+[m[32m=======[m
[32m+[m[32m    tools:context=".MainActivity">[m
[32m+[m
[32m+[m[32m    <TextView[m
[32m+[m[32m        android:layout_width="wrap_content"[m
[32m+[m[32m        android:layout_height="wrap_content"[m
[32m+[m[32m        android:text="Hello World!"[m
[32m+[m[32m>>>>>>> clean-start[m
         app:layout_constraintBottom_toBottomOf="parent"[m
         app:layout_constraintEnd_toEndOf="parent"[m
         app:layout_constraintStart_toStartOf="parent"[m
         app:layout_constraintTop_toTopOf="parent" />[m
 [m
[32m+[m[32m<<<<<<< HEAD[m
     <!-- Version Info (Optional) -->[m
     <TextView[m
         android:id="@+id/tvVersion"[m
[36m@@ -191,4 +201,6 @@[m
         app:layout_constraintEnd_toEndOf="parent"[m
         app:layout_constraintStart_toStartOf="parent" />[m
 [m
[32m+[m[32m=======[m
[32m+[m[32m>>>>>>> clean-start[m
 </androidx.constraintlayout.widget.ConstraintLayout>[m
\ No newline at end of file[m
