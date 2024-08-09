package com.example.ptranslater

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.MotionButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var sourceLanguageEt: EditText
    private lateinit var targetLanguageTv: TextView
    private lateinit var  sourceLanguageChooseBtn: MotionButton
    private lateinit var targetLanguageChoseBtn: MotionButton
    private lateinit var translateBtn:MotionButton

    companion object{
        private  const val TAG = "MAIN_TAG"
    }

    private var languageArrayList: ArrayList<ModelLanguage>?=null

    private var sourceLanguageCode ="en"
    private var sourceLanguageTitle ="English"
    private var targetLanguageCode = "ur"
    private var targetLanguageTitle = "Urdu"

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator
    private lateinit var progressDialog: ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt)
        targetLanguageTv = findViewById(R.id.targetLanguageTv)
        sourceLanguageChooseBtn= findViewById(R.id.sourceLanguageChooseBtn)
        targetLanguageChoseBtn= findViewById(R.id.targetLanguageChooseBtn)
        translateBtn = findViewById(R.id.translateBtn)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wati")
        progressDialog.setCanceledOnTouchOutside(false)

        loadAvailableLanguages()

        sourceLanguageChooseBtn.setOnClickListener {
            sourceLanguageChoose()

        }

        targetLanguageChoseBtn.setOnClickListener {
            targetLanguageChoose()
        }
        translateBtn.setOnClickListener {
            validateData()

        }




    }

    private var sourceLanguageText = ""
    private fun validateData() {
        
        sourceLanguageText = sourceLanguageEt.text.toString().trim()

        Log.d(TAG, "validateData: sourceLanguageText: $sourceLanguageText")

        if(sourceLanguageText.isEmpty()){
            showToast("Put the text in Prachi's mind to translate....")
        }
        else{
            startTranslation()
        }

    }

    private fun startTranslation(){

        progressDialog.setMessage("Ruk jao Prachi kar rahi hai kamchor hai na thoda sa time laga ga....")
        progressDialog.show()

        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()
        translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {

                Log.d(TAG, "startTranslation: model ready, Prachi is starting translation....")

                progressDialog.setMessage("Prachi Translating....")

                translator.translate(sourceLanguageText)
                    .addOnSuccessListener {translatedText->

                        Log.d(TAG, "startTranslation: translatedText:$translatedText")

                        progressDialog.dismiss()

                        targetLanguageTv.text = translatedText
                    }
            }
            .addOnFailureListener {e->

                progressDialog.dismiss()
                Log.e(TAG, "startTranslation: ",e )

                showToast("Failed to translate(Prachi ka dimag blasted hogaya) due to ${e.message}")
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Log.e(TAG, "startTranslation: ", e)

                showToast("Failed dur to ${e.message}")
            }
    }


    private fun loadAvailableLanguages(){

        languageArrayList = ArrayList()


        val languageCodeList = TranslateLanguage.getAllLanguages()


        for (languageCode in languageCodeList){

            val languageTitle = Locale(languageCode).displayLanguage

            Log.d(TAG, "loadAvailableLanguages: languageCode: $languageCode")
            Log.d(TAG, "loadAvailableLanguages: language: $languageTitle")

            val modelLanguage = ModelLanguage(languageCode, languageTitle)

            languageArrayList!!.add(modelLanguage)
        }
    }

    private fun sourceLanguageChoose(){

        val popupMenu = PopupMenu(this, sourceLanguageChooseBtn)

        for(i in languageArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE,i,i,languageArrayList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val position = menuItem.itemId

            sourceLanguageCode = languageArrayList!![position].languageCode
            sourceLanguageTitle = languageArrayList!![position].languageTitle

            sourceLanguageChooseBtn.text = sourceLanguageTitle
            sourceLanguageEt.hint = "Enter $sourceLanguageTitle"

            Log.d(TAG, "sourceLanguageChoose: sourceLanguageCode: $sourceLanguageCode")
            Log.d(TAG, "sourceLanguageChoose: sourceLanguageTitle: $sourceLanguageTitle")
            false
        }
    }

    private fun targetLanguageChoose(){

        val popupMenu = PopupMenu(this, targetLanguageChoseBtn)

        for (i in languageArrayList!!.indices){
            popupMenu.menu.add(Menu.NONE,i ,i, languageArrayList!![i].languageTitle)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem->

            val position = menuItem.itemId


            targetLanguageCode = languageArrayList!![position].languageCode
             targetLanguageTitle = languageArrayList!![position].languageTitle

            targetLanguageChoseBtn.text = targetLanguageTitle

            Log.d(TAG, "targetLanguageChoose: targetLanguageCode: $targetLanguageCode")
            Log.d(TAG, "targetLanguageChoose: targetLanguageTitle: $targetLanguageTitle")



            false
        }
    }

    private  fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}


