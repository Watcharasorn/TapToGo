package com.reader.client

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.reader.service.IReaderSdkListener
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import kotlinx.android.synthetic.main.activity_update.tv_msg
import timber.log.Timber
import java.io.File


class UpdateActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来自aar
    private val REQUEST_FILE_BIN = 1 //mcu文件信号
    private val REQUEST_FILE_ZIP = 2 //linux文件信号

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        //返回结果
        pos.register(applicationContext, IReaderSdkListener { data ->
            runOnUiThread{
                //服务主动返回消息
                //升级
                if(data.containsKey(ReaderConstant.KEY_MODULE) && data.containsKey(ReaderConstant.KEY_RESULT_MSG)){
                    tv_msg.text = "result: ${data[ReaderConstant.KEY_RESULT_MSG]}"
                }else{
                    tv_msg.text = "$data"
                }
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }
    fun updateIc(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        // 设置文件类型过滤器，筛选 .bin 和 .zip 文件
        val mimeTypes = arrayOf("application/octet-stream")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent,REQUEST_FILE_BIN)
    }

    fun updateSystem(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        // 设置文件类型过滤器，筛选 .bin 和 .zip 文件
        val mimeTypes = arrayOf( "application/zip")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent,REQUEST_FILE_ZIP)
    }
    fun downloadLog(view: View) {
        //下载日志
        pos.downloadLog()
    }
    fun reboot(view: View) {
        pos.device_reboot()
    }


    fun back(view: View){
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.i("requestCode:$requestCode resultCode:$resultCode  ")
        if(requestCode == REQUEST_FILE_BIN && resultCode == RESULT_OK){
            val uri = data!!.data
            val filePath = getPath(this,uri)
            Timber.i("bin filePath:$filePath")
            val f = File(filePath)
            pos.device_upDateIc(f)
        }
        if(requestCode == REQUEST_FILE_ZIP  && resultCode == RESULT_OK ){ //
            val uri = data!!.data
            val filePath = getPath(this,uri)
            Timber.i("zip filePath:$filePath")
            val f = File(filePath)
            pos.device_upDateSystem(f)
//            pos.device_upDateTest(f)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun getPath(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val id = DocumentsContract.getDocumentId(uri).replace("[^0-9]".toRegex(), "")
//                Log.d(TAG, id)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                return getDataColumn(context, contentUri, null, null)
            } else if ("com.android.providers.media.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }
}