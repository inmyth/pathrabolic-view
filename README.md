pathrabolic-view
================

Path-like view for Android


To use:

Add this section to the layout XML. It must be wrapped in FrameLayout
```xml
    <com.pathrabolic.PathrabolicView
        android:id="@+id/pbv"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

And in the code
```java
		PathrabolicView a = (PathrabolicView) findViewById(R.id.pbv);
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "first entry");
		map.put(1, "another button");
		map.put(2, "yet another button");
		a.init(map);
```



Credit:
daCapricorn for the animations
