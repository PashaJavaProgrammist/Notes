package com.dev.pavelharetskiy.notes_kotlin.activities

import android.Manifest
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.dev.pavelharetskiy.notes_kotlin.ANDROID_KEY_STORE
import com.dev.pavelharetskiy.notes_kotlin.KEY_NAME
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.utils.FingerprintHandler
import kotlinx.android.synthetic.main.activity_fingerprint.*
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class FingerprintActivity : AppCompatActivity() {

    private lateinit var cipher: Cipher

    // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
    private var keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    private lateinit var keyGenerator: KeyGenerator
    private lateinit var cryptoObject: FingerprintManager.CryptoObject
    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var keyguardManager: KeyguardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)
    }

    override fun onResume() {
        super.onResume()

        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                tv_info.text = getString(R.string.support_not)
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                tv_info.text = getString(R.string.enable_perm_finger)
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                tv_info.text = getString(R.string.no_fingerprint_confg)
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                tv_info.text = getString(R.string.enable_lockscreen_security)
            } else {
                try {
                    generateKey()
                } catch (e: FingerprintException) {
                    e.printStackTrace()
                }

                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    cryptoObject = FingerprintManager.CryptoObject(cipher)

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    val helper = FingerprintHandler(this)
                    helper.startAuth(fingerprintManager, cryptoObject)
                }
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    @TargetApi(Build.VERSION_CODES.M)
    @Throws(FingerprintException::class)
    private fun generateKey() {
        try {

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

            //Initialize an empty KeyStore//
            keyStore.load(null)

            //Initialize the KeyGenerator//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator.init(
                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                                //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                                .setUserAuthenticationRequired(true)
                                .setEncryptionPaddings(
                                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                .build())
            }

            //Generate the key//
            keyGenerator.generateKey()

        } catch (exc: Exception) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: CertificateException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        } catch (exc: IOException) {
            exc.printStackTrace()
            throw FingerprintException(exc)
        }

    }

    //Create a new method that we’ll use to initialize our cipher//
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun initCipher(): Boolean {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        }

        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            //Return true if the cipher has been initialized successfully//
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {

            //Return false if cipher initialization failed//
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: CertificateException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: IOException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(getString(R.string.failed_to_init_chipper), e)
        }

    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private inner class FingerprintException(e: Exception) : Exception(e)
}
