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

Mobile.tap(findTestObject('Object Repository/Swipe Right/android.widget.Button - NEXT'), 0)

Mobile.tap(findTestObject('Object Repository/Swipe Right/android.widget.Button - CONTINUE WITH EMAIL'), 0)

Mobile.setText(findTestObject('Object Repository/Swipe Right/android.widget.EditText - Email'), 'nguyenkhactan220502@gmail.com', 
    0)

Mobile.setText(findTestObject('Object Repository/Swipe Right/android.widget.EditText - Password'), '123456789', 0)

Mobile.tap(findTestObject('Object Repository/Swipe Right/android.widget.Button - Login'), 0)

 
'Storing the startX,endX values by dividing device height by 2 Because Y coordinates are constant'
 
int startY = device_Height / 2
 
 
 
'Here endY and startY values are equal for vertical Swiping for that assigning startY value to endY'
 
int endY = startY
 
 
'Storing the startX value'
 
int startX = device_Width * 0.30
 
 
 
'Storing the endX value'
 
int endX = device_Width * 0.70
 
 
 
'Here Y constant for Swipe Vertical Left to Right'
 
Mobile.swipe(startX, startY, endX, endY)

Mobile.pressBack()

Mobile.pressBack()

Mobile.closeApplication()

