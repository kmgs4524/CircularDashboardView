# CircularDashboardView
![GitHub release (latest by date)](https://img.shields.io/github/v/release/kmgs4524/CircularDashboardView)


![demo_image](https://github.com/kmgs4524/CircularDashboardView/blob/master/demo_image.png)

## Gradle Setup
build.gradle(project)
```
repositories {
  maven { url 'https://jitpack.io' }
}
```
build.gradle(app)
```
dependencies {
  implementation 'com.github.kmgs4524:CircularDashboardView:v0.0.1'
}
```
## Usage
```
<com.york.circulardashboardview.CircularDashboardView
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:labelText="battery"
    app:labelTextSize="18sp"
    app:layout_constraintStart_toStartOf="parent"
    app:percent="90"
    app:percentTextSize="30sp" />
```
