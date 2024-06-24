package com.example.cancer

import android.annotation.SuppressLint
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "cancer.tflite"

    private lateinit var resultText: TextView
    private lateinit var Age: EditText
    private lateinit var Gender: EditText
    private lateinit var BMI: EditText
    private lateinit var Smoking: EditText
    private lateinit var GeneticRisk: EditText
    private lateinit var PhysicalActivity: EditText
    private lateinit var AlcoholIntake: EditText
    private lateinit var CancerHistory: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        Age = findViewById(R.id.Age)
        Gender = findViewById(R.id.Gender)
        BMI = findViewById(R.id.BMI)
        Smoking = findViewById(R.id.Smoking)
        GeneticRisk = findViewById(R.id.GeneticRisk)
        PhysicalActivity = findViewById(R.id.PhysicalActivity)
        AlcoholIntake = findViewById(R.id.AlcoholIntake)
        CancerHistory = findViewById(R.id.CancerHistory)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Age.text.toString(),
                Gender.text.toString(),
                BMI.text.toString(),
                Smoking.text.toString(),
                GeneticRisk.text.toString(),
                PhysicalActivity.text.toString(),
                AlcoholIntake.text.toString(),
                CancerHistory.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Yes"
                }else if (result == 1){
                    resultText.text = "No"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(9)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}