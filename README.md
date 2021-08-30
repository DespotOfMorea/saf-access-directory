# Storage Access Framework for accessing directory (on Android 11+)

Simple example how to access files from specific directory on device, using Storage Access Framework, on Android 11+, without any permission. Once User allow your App access to specific directory, uri will be saved in shared preferences, for later use.

File (<i>chant.ogs</i>) used in this example is plain text, but this way will work for any type of files.

Since <i>startActivityForResult()</i> method is now deprecated and use of <i>Activity Result API</i> is strongly recommended, both ways are represented in code.

## Further reading:
* [Data and file storage overview](https://developer.android.com/training/data-storage)
* [Access documents and other files from shared storage](https://developer.android.com/training/data-storage/shared/documents-files)
* [Getting a result from an activity](https://developer.android.com/training/basics/intents/result)
