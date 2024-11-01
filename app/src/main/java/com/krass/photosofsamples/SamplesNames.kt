package com.krass.photosofsamples

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.io.File

class SamplesNames private constructor() {
    private lateinit var machines: SnapshotStateList<Pair<String, String>>
    companion object {

        @Volatile private var instance: SamplesNames? = null // Volatile modifier is necessary

        fun getInstance() =
            instance ?: synchronized(this) { // synchronized to avoid concurrency problem
                instance ?: SamplesNames().also { instance = it }
            }

        private const val SAMPLES_NAMES_STRING = "MP_27-01-01_TC(flexch159)|" +
                "MP_27-01-01_YMR(flexch159)|" +
                "MP_27-01-03_TC(flexch124)|" +
                "MP_27-01-03_YMR(flexch124)|" +
                "MP_27-01-06_TC(flexch105)|" +
                "MP_27-01-06_YMR(flexch105)|" +
                "MP_27-01-08_TC(flexct165)|" +
                "MP_27-01-08_YMR(flexct165)|" +
                "MP_27-01-12_TC(flexct129)|" +
                "MP_27-01-12_YMR(flexct129)|" +
                "MP_27-01-13_ТС(flexct131)|" +
                "MP_27-01-13_YMR(flexct131)|" +
                "MP_27-01-16_TC(flexcs4)|" +
                "MP_27-01-16_YMR(flexcs4)|" +
                "MP_27-01-18_TC(not connected)|" +
                "MP_27-01-18_YMR(not connected)|" +
                "MP_38-01-01_TC(flexch163)|" +
                "MP_38-01-01_YMR(flexch163)|" +
                "MP_38-01-07_TC(flexch140)|" +
                "MP_38-01-07_YMR(flexch140)|" +
                "MP_38-01-10_TC(flexct162)|" +
                "MP_38-01-10_YMR(flexct162)|" +
                "MP_62-01-01_TC(not connected)|" +
                "MP_62-01-01_YMR(not connected)|" +
                "MP_62-01-02_TC(flexca144)|" +
                "MP_62-01-02_YMR(flexca144)|" +
                "MP_62-01-03_TC(flexch182)|" +
                "MP_62-01-03_YMR(flexch182)|" +
                "MP_64-01-02_TC(flexch141)|" +
                "MP_64-01-02_YMR(flexch141)|" +
                "MP_83-01-01_TC(flexct73)|" +
                "MP_83-01-01_YMR(flexct73)|" +
                "MP_20-01-03_TC_DI(flexca141)|" +
                "MP_20-01-03_YMR_DI(flexca141)|" +
                "MP_20-01-03_TC_PG(flexca141)|" +
                "MP_20-01-03_YMR_PG(flexca141)|" +
                "NPI_20-01-01_TC_DI(ca39)|" +
                "NPI_20-01-01_YMR_DI(ca39)|" +
                "NPI_20-01-01_TC_PG(ca39)|" +
                "NPI_20-01-01_YMR_PG(ca39)"

        private fun getSamplesOfMachinesString(context: Context): String? {
            val clearStart = false
            val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val machines = sharedPref.getString("machines", "")
            if (machines != null && machines.isEmpty() || clearStart) {
                with(sharedPref.edit()) {
                    putString("machines", SAMPLES_NAMES_STRING)
                    apply()
                }
                return SAMPLES_NAMES_STRING
            }
            return machines
        }

        fun storeMachines(
            context: Context,
            machines: SnapshotStateList<Pair<String, String>>
        ) {
            machines.removeIf{(key, value) -> key.isEmpty() && value.isEmpty()}
            var machinesString = ""
            machines.forEach{
                machinesString += "${it.first}(${it.second})|"
            }
            val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("machines", machinesString.removeSuffix("|"))
                apply()
            }

        }

        fun samplePairToFileName(pair: Pair<String, String>): String {
            return "${pair.first}(${pair.second})"
        }

        fun toShortMachineName(machineName: String): String {
            var shortName: String
            val tmp: List<String> = machineName.split("[_]".toRegex())
            if (tmp.size < 3) {
                return machineName
            }
            shortName = tmp[1]
            val tmp2: List<String> = shortName.split("[-]".toRegex()) //3,4
            if (tmp2.size < 3) {
                return shortName
            }
            shortName = tmp2[0] + "-" + tmp2[2] + " " + tmp[2]
            if (tmp.size == 4) {
                shortName += " " + tmp[3]
            }
            return shortName
        }
//        deleteFile: content://media/external/images/media/1000057413
//        14:00:19.555  D  deleteFile 2: /storage/emulated/0/Pictures/Samples 23/CameraxImage.jpeg
        fun deleteFile(uri: Uri, context: Context){
            Log.d("deleteFile", "deleteFile: " + uri.toString())
            val filePath = getFilePath(uri, context)
            Log.d("deleteFile 2", "deleteFile 2: " + filePath.toString())
            if(filePath != null) {
                val fdelete = File(filePath)
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        println("file Deleted :")
                    } else {
                        println("file not Deleted :")
                    }
                }
            }
        }

        private fun getFilePath(uri: Uri, context: Context): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)

            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()

                val columnIndex = cursor.getColumnIndex(projection[0])
                val picturePath = cursor.getString(columnIndex) // returns null
                cursor.close()
                return picturePath
            } else if(uri.toString().isNotEmpty()) {
                return Uri.parse(uri.path).toString()
            } else {
                return null
            }
        }
    }

    fun getMachines(context: Context): SnapshotStateList<Pair<String, String>> {
        if (!this::machines.isInitialized) {
            initMachines(context)
        }
        return machines
    }

    private fun initMachines(context: Context) {
        if (!this::machines.isInitialized) {
            machines = mutableStateListOf<Pair<String, String>>()
            val machinesString = getSamplesOfMachinesString(context)
            machinesString?.split("[|]".toRegex())?.forEach {
                val (key, value) = it.split("[(]".toRegex())
                machines.add(Pair(key, value.removeSuffix(")")))
            }
            if (!machines.isEmpty()) {
                val tmp = machines.sortedBy { (key, value) -> key }.toMutableStateList()
                machines.clear()
                machines.addAll(tmp)
            }
        }
    }
}

//val fileNames = listOf(
//    "MP_27-01-01_TC(flexch159)",
//    "MP_27-01-01_YMR(flexch159)",
//    "MP_27-01-03_TC(flexch124)",
//    "MP_27-01-03_YMR(flexch124)",
//    "MP_27-01-06_TC(flexch105)",
//    "MP_27-01-06_YMR(flexch105)",
//    "MP_27-01-08_TC(flexct165)",
//    "MP_27-01-08_YMR(flexct165)",
//    "MP_27-01-12_TC(flexct129)",
//    "MP_27-01-12_YMR(flexct129)",
//    "MP_27-01-13_ТС(flexct131)",
//    "MP_27-01-13_YMR(flexct131)",
//    "MP_27-01-16_TC(flexcs4)",
//    "MP_27-01-16_YMR(flexcs4)",
//    "MP_27-01-18_TC(not connected)",
//    "MP_27-01-18_YMR(not connected)",
//    "MP_38-01-01_TC(flexch163)",
//    "MP_38-01-01_YMR(flexch163)",
//    "MP_38-01-07_TC(flexch140)",
//    "MP_38-01-07_YMR(flexch140)",
//    "MP_38-01-10_TC(flexct162)",
//    "MP_38-01-10_YMR(flexct162)",
//    "MP_62-01-01_TC(not connected)",
//    "MP_62-01-01_YMR(not connected)",
//    "MP_62-01-02_TC(flexca144)",
//    "MP_62-01-02_YMR(flexca144)",
//    "MP_62-01-03_TC(flexch182)",
//    "MP_62-01-03_YMR(flexch182)",
//    "MP_64-01-02_TC(flexch141)",
//    "MP_64-01-02_YMR(flexch141)",
//    "MP_83-01-01_TC(flexct73)",
//    "MP_83-01-01_YMR(flexct73)",
//    "MP_20-01-03_TC_DO(flexca141)",
//    "MP_20-01-03_YMR_DO(flexca141)",
//    "MP_20-01-03_TC_PG(flexca141)",
//    "MP_20-01-03_YMR_PG(flexca141)",
//    "NPI_20-01-01_TC_DO(ca39)",
//    "NPI_20-01-01_YMR_DO(ca39)",
//    "NPI_20-01-01_TC_PG(ca39)",
//    "NPI_20-01-01_YMR_PG(ca39)"
//)



//fun initMachinesMap(context: Context, machines: SnapshotStateList<Pair<String, String>>) {
//    val machinesString = getSamplesOfMachinesString(context)
//    if (machinesString != null) {
//        machinesString.split("[|]".toRegex()).forEach {
//            val (key, value) = it.split("[(]".toRegex())
//            machines.add(Pair(key, value.removeSuffix(")")))
//        }
//    }
//    if (!machines.isEmpty()) {
//        val tmp = machines.sortedBy { (key, value) -> key }.toMutableStateList()
//        machines.clear()
//        machines.addAll(tmp)
//    }
//}

//private fun getSamplesOfMachinesString(context: Context): String? {
//    val clearStart = false
//    val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
//    val machines = sharedPref.getString("machines", "")
//    if (machines != null && machines.isEmpty() || clearStart) {
//        with (sharedPref.edit()) {
//            putString("machines", com.krass.liquidtestphoto.sampleMachinesString)
//            apply()
//        }
//        return sampleMachinesString
//    }
//    return machines
//}

//fun setSamplesOfMachines(context: Context, machines: SnapshotStateList<Pair<String, String>>){
//    var machinesString = ""
//    machines.forEach {
//        machinesString += "${it.first}(${it.second})|"
//    }
//    val sharedPref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
//    with (sharedPref.edit()) {
//        putString("machines", machinesString.removeSuffix("|"))
//        apply()
//    }
//
//}