import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

Mobile.startExistingApplication('com.example.tinderforit')

Mobile.tap(findTestObject('Object Repository/Register Email already exists/android.widget.Button - NEXT'), 0)

Mobile.tap(findTestObject('Object Repository/Register Email already exists/android.widget.Button - CONTINUE WITH EMAIL'), 
    0)

Mobile.tap(findTestObject('Object Repository/Register Email already exists/android.widget.Button - Dont have an account Register here'), 
    0)

Mobile.setText(findTestObject('Object Repository/Register Email already exists/android.widget.EditText - Email'), email, 
    0)

Mobile.setText(findTestObject('Object Repository/Register Email already exists/android.widget.EditText - Password'), password, 
    0)

Mobile.setText(findTestObject('Object Repository/Register Email already exists/android.widget.EditText - Confirm Password'), 
    confirm, 0)

Mobile.tap(findTestObject('Object Repository/Register Email already exists/android.widget.Button - Register'), 0)

Mobile.pressBack()

Mobile.pressBack()

Mobile.pressBack()

Mobile.closeApplication()

