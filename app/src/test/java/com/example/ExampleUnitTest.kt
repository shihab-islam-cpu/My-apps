package com.example

import com.example.data.model.ClientWebsite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun clientWebsite_entityPropertiesTest() {
    val now = System.currentTimeMillis()
    val website = ClientWebsite(
      id = 1L,
      websiteName = "TechSphere IT Agency",
      clientName = "Ahmad Enterprises",
      url = "https://example.com/techsphere",
      status = "Live",
      lastUpdateTime = now,
      techStack = "Next.js 14, Tailwind CSS",
      description = "Corporate consulting portal"
    )

    assertEquals("TechSphere IT Agency", website.websiteName)
    assertEquals("Ahmad Enterprises", website.clientName)
    assertEquals("https://example.com/techsphere", website.url)
    assertEquals("Live", website.status)
    assertEquals(now, website.lastUpdateTime)
    assertTrue(website.url.startsWith("https://"))
  }

  @Test
  fun clientWebsite_statusUpdateTest() {
    val initialTime = System.currentTimeMillis() - 10000
    val website = ClientWebsite(
      id = 2L,
      websiteName = "Nexus Fashion Store",
      clientName = "StyleCraft",
      url = "https://example.com/nexus",
      status = "In Development",
      lastUpdateTime = initialTime
    )

    val updatedTime = System.currentTimeMillis()
    val updated = website.copy(status = "Live", lastUpdateTime = updatedTime)

    assertEquals("Live", updated.status)
    assertTrue(updated.lastUpdateTime >= initialTime)
  }
}
