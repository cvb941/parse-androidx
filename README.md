# Parse Server LiveData, Paging DataSource
This small library provides implementations for AndroidX components, with the [Parse Server](https://parseplatform.org/).

These require the [Live Query](https://docs.parseplatform.org/parse-server/guide/#live-queries) set-up on the Parse Server in order to subscribe to changes made in the Parse objects.

## ParseLiveData
Takes in a ParseQuery parameter. Subscribes to the query and refreshes its value when the underlying ParseQuery result changes.

```kotlin
fruitLiveData = ParseLiveData(ParseQuery.getQuery(Fruit::class.java).whereMatches("name", "apple"))

fruitLiveData.observe(viewLifecycleOwner, Observer {
    binding.fruit = it // Do something
})
```

## ParseDataSourceFactory
Based on this [codepath guide](https://github.com/codepath/android_guides/wiki/Building-Data-driven-Apps-with-Parse#using-with-android-paging-library), this class provides a Paging DataSource backed by the Parse Server. It properly handles the respective paging calls and produces a PagedList, which can be submitted to PagedListAdapter, to be used in RecyclerViews.

This way, the items in the list can be automatically refreshed when the query content changes, with the PagedListAdapter handling delta updates, inserts and deletions properly.


```kotlin
val fruitsQuery = ParseQuery.getQuery(Fruit::class.java)
val config = PagedList.Config.Builder().setPageSize(20).build()
val sourceFactory = ParseDataSourceFactory(fruitsQuery)
val items = LivePagedListBuilder(sourceFactory, config).build()

items.observe(viewLifecycleOwner, Observer {
    (rvFruits.adapter as FruitAdapter).submitList(it) // Submit PagedList to PagedListAdapter
})
```

## Installation

```gradle
repositories {
    maven { url "https://jitpack.io" } // 1. Add JitPack repository
}

// 2. Add this library
implementation 'com.github.cvb941:parse-androidx:v1.0'
```