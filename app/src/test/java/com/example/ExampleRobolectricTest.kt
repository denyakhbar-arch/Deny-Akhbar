package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DARcorporate", appName)
  }

  @Test
  fun `biometric face registration and attendance verification`() {
    val repository = com.example.data.DarcorpRepository()
    
    // Check initial pre-seeded biometric profile for CEO
    val ceoProfile = repository.getFaceProfile("usr_dirut")
    org.junit.Assert.assertNotNull(ceoProfile)
    org.junit.Assert.assertTrue(ceoProfile!!.isRegistered)
    org.junit.Assert.assertTrue(ceoProfile.antiSpoofingPassed)

    // Register face profile for temporary staff
    val newProfile = repository.registerFaceProfile(
        userId = "usr_temp_1",
        userName = "Joko Santoso",
        confidence = 99.6f
    )
    org.junit.Assert.assertTrue(newProfile.isRegistered)
    org.junit.Assert.assertTrue(newProfile.biometricHash.startsWith("DAR-BIO-"))
    org.junit.Assert.assertEquals(99.6f, newProfile.confidenceScore, 0.01f)
    org.junit.Assert.assertTrue(repository.isFaceRegistered("usr_temp_1"))
  }

  @Test
  fun `paid services catalog and direct virtual account payment`() {
    val repository = com.example.data.DarcorpRepository()

    // 1. Validate payment channels: SeaBank, Dana, Ovo, Gopay, BCA VA, Mandiri VA, BRI VA, QRIS
    val channels = repository.paymentChannels
    org.junit.Assert.assertTrue(channels.any { it.code == "VA_SEABANK" && it.accountNumber == "901118227810" })
    org.junit.Assert.assertTrue(channels.any { it.code == "EWAL_DANA" && it.accountNumber == "0895345657270" })
    org.junit.Assert.assertTrue(channels.any { it.code == "EWAL_OVO" && it.accountNumber == "0895345657270" })
    org.junit.Assert.assertTrue(channels.any { it.code == "EWAL_GOPAY" && it.accountNumber == "087765483134" })
    org.junit.Assert.assertTrue(channels.any { it.code == "VA_BCA" && it.accountNumber == "8277 0812 3456 7890" })
    org.junit.Assert.assertTrue(channels.any { it.code == "QRIS" })

    // 2. Validate paid services catalog for Perorangan and Non-Mitra
    val services = repository.paidServicesCatalog
    val peroranganServices = services.filter { it.targetAudience == com.example.model.ServiceTargetAudience.PERORANGAN }
    val nonMitraServices = services.filter { it.targetAudience == com.example.model.ServiceTargetAudience.BADAN_USAHA_NON_MITRA }

    org.junit.Assert.assertTrue(peroranganServices.isNotEmpty())
    org.junit.Assert.assertTrue(nonMitraServices.isNotEmpty())

    // 3. Process direct payment for an individual client with proof of payment attachment
    val consultService = peroranganServices.first()
    val seabankChannel = channels.first { it.code == "VA_SEABANK" }
    val invoice = repository.processDirectPayment(
        service = consultService,
        channel = seabankChannel,
        customerName = "Bapak Hendri (Klien Pribadi)",
        customerType = "Pribadi Perorangan",
        proofType = "Kamera Langsung",
        proofUri = "content://media/external/images/media/42",
        proofNotes = "Struk transfer SeaBank No. 901118227810"
    )

    org.junit.Assert.assertEquals(com.example.model.PaymentStatus.PAID, invoice.status)
    org.junit.Assert.assertEquals("901118227810", invoice.channel.accountNumber)
    org.junit.Assert.assertTrue(invoice.hasProofUploaded)
    org.junit.Assert.assertEquals("Kamera Langsung", invoice.proofAttachmentType)
    org.junit.Assert.assertEquals("Struk transfer SeaBank No. 901118227810", invoice.proofNotes)
    org.junit.Assert.assertTrue(repository.serviceInvoices.value.contains(invoice))
  }

  @Test
  fun `company profile pdf asset and 13-page topics validation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // 1. Verify asset file company_profile_dar.pdf exists and is non-empty
    val assetStream = context.assets.open("company_profile_dar.pdf")
    org.junit.Assert.assertNotNull(assetStream)
    val availableBytes = assetStream.available()
    org.junit.Assert.assertTrue(availableBytes > 1000)
    assetStream.close()

    // 2. Verify all 13 pages of Company Profile Topics are mapped
    val topics = com.example.ui.components.COMPANY_PROFILE_TOPICS
    org.junit.Assert.assertEquals(13, topics.size)
    org.junit.Assert.assertEquals(0, topics.first().pageIndex)
    org.junit.Assert.assertEquals(12, topics.last().pageIndex)
    org.junit.Assert.assertTrue(topics.any { it.fullTitle.contains("PT. Satria Saftindo Jaya") })
    org.junit.Assert.assertTrue(topics.any { it.fullTitle.contains("CV. Sejahtera Mandiri Metal") })
    org.junit.Assert.assertTrue(topics.any { it.fullTitle.contains("PT. Infiniti Sumber Alam") })
    org.junit.Assert.assertTrue(topics.any { it.fullTitle.contains("Yayasan Purgatorio") })
  }
}
