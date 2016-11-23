package io.ticktag.restinterface.timecategory


import io.ticktag.*
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.timecategory.controllers.TimeCategoryController
import io.ticktag.restinterface.timecategory.schema.CreateTimeCategoryRequestJson
import io.ticktag.restinterface.timecategory.schema.UpdateTimeCategoryRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import javax.inject.Inject

class TimeCategoryApiTest : ApiBaseTest() {
    @Inject lateinit var assignmentTagController: TimeCategoryController
    var name = "0815Activity"
    var name2 = "1337Activity"

    /*Create Tests*/
    @Test
    fun `create timecategory with Admin and not member should succeed`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(ADMIN_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name))
        }

    }

    @Test
    fun `create timecategory with User as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_UOA_ID
        withUser(USER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
            assignmentTagController.listProjectTimeCategories(projectId)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name))
        }

    }

    @Test
    fun `create timecategory with Observer as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_OAU_ID
        withUser(OBSERVER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
            assignmentTagController.listProjectTimeCategories(projectId)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name))
        }

    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory with User as Observer should fail`() {
        val projectId = PROJECT_AOU_AUO_ID
        withUser(USER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory with User as User should fail`() {
        val projectId = PROJECT_AOU_OAU_ID
        withUser(USER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory with Observer as Observer should fail`() {
        val projectId = PROJECT_AOU_UOA_ID
        withUser(OBSERVER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory with User and not member should fail`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(USER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory with Observer and not member should fail`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(OBSERVER_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create timecategory anonymous should fail`() {
        val projectId = PROJECT_AOU_AUO_ID
        withoutUser {
            val req = CreateTimeCategoryRequestJson(projectId, name)
            assignmentTagController.createTimeCategory(req)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `create timecategory with wrong data should fail`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(ADMIN_USER_ID) { principal ->
            val req = CreateTimeCategoryRequestJson(projectId, "")
            assignmentTagController.createTimeCategory(req)
        }
    }

    /*Update  Tests*/
    @Test
    fun `update timecategory with Admin and not member should succeed`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(ADMIN_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name2)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first(), req)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name2))
        }

    }

    @Test
    fun `update timecategory with User as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_UOA_ID
        withUser(USER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name2)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first(), req)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name2))
        }

    }

    @Test
    fun `update timecategory with Observer as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_OAU_ID
        withUser(OBSERVER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name2)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first(), req)
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            result ?: fail()
            assertThat(result?.name, `is`(name2))
        }

    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory with User as Observer should fail`() {
        withUser(USER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first(), req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory with User as User should fail`() {
        withUser(USER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first(), req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory with Observer as Observer should fail`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first(), req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory with User and not member should fail`() {
        withUser(USER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first(), req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory with Observer and not member should fail`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first(), req)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `update timecategory anonymous should fail`() {
        withoutUser {
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first(), req)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `update timecategory with wrong data should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson("")
            assignmentTagController.updateTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first(), req)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `update unexisting timecategory should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            val req = UpdateTimeCategoryRequestJson(name)
            assignmentTagController.updateTimeCategory(PROJECT_NO_MEMBERS_ID, req)
        }
    }

    /*Get  Tests*/
    @Test
    fun `get timecategory with Admin and not member should succeed`() {
        withUser(ADMIN_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }
    }

    @Test
    fun `get timecategory with Observer and not member should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }
    }


    @Test
    fun `get timecategory with User as ProjectAdmin should succeed`() {
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    @Test
    fun `get timecategory with User as ProjectObserver should succeed`() {
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    fun `get timecategory with User as ProjectUser should succeed`() {
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    @Test
    fun `get timecategory with Observer as ProjectAdmin should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    @Test
    fun `get timecategory with Observer as ProjectObserver should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    @Test
    fun `get timecategory with Observer as ProjectUser should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first())
            assertThat(timecat.name, `is`(TIMECAT_CONTENT.first()))
        }

    }

    @Test(expected = AccessDeniedException::class)
    fun `get timecategory with User and not member should fail`() {
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.getTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `get timecategory anonymous should fail`() {
        withoutUser {
            assignmentTagController.getTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first())
        }
    }

    /*List Tests*/
    @Test
    fun `list timecategory with Admin and not member should succeed`() {
        val timecatIds = TIMECAT_PROJECT_NO_MEMBERS_IDS
        withUser(ADMIN_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_NO_MEMBERS_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }
    }

    @Test
    fun `list timecategory with Observer and not member should succeed`() {
        val timecatIds = TIMECAT_PROJECT_NO_MEMBERS_IDS
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_NO_MEMBERS_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }
    }

    @Test
    fun `list timecategory with User as ProjectAdmin should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_UOA_IDS
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_UOA_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    @Test
    fun `list timecategory with User as ProjectObserver should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_AUO_IDS
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_AUO_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    fun `list timecategory with User as ProjectUser should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_OAU_IDS
        withUser(USER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_OAU_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    @Test
    fun `list timecategory with Observer as ProjectAdmin should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_OAU_IDS
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_OAU_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    @Test
    fun `list timecategory with Observer as ProjectObserver should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_UOA_IDS
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_UOA_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    @Test
    fun `list timecategory with Observer as ProjectUser should succeed`() {
        val timecatIds = TIMECAT_PROJECT_AOU_AUO_IDS
        withUser(OBSERVER_USER_ID) { principal ->
            val timecat = assignmentTagController.listProjectTimeCategories(PROJECT_AOU_AUO_ID)
            assertThat(timecat.size, `is`(timecatIds.size))
            assertThat(timecat.first().id, `is`(timecatIds.first()))
            assertThat(timecat.last().id, `is`(timecatIds.last()))
        }

    }

    @Test(expected = AccessDeniedException::class)
    fun `list timecategory with User and not member should fail`() {
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.listProjectTimeCategories(PROJECT_NO_MEMBERS_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `list timecategory anonymous should fail`() {
        withoutUser {
            assignmentTagController.listProjectTimeCategories(TIMECAT_PROJECT_AOU_AUO_IDS.first())
        }
    }

    /*Delete Tests*/
    @Test
    fun `delete timecategory with Admin and not member should succeed`() {
        val projectId = PROJECT_NO_MEMBERS_ID
        withUser(ADMIN_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            if (result != null) fail()
        }

    }

    @Test
    fun `delete timecategory with User as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_UOA_ID
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first())
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            if (result != null) fail()
        }

    }

    @Test
    fun `delete timecategory with Observer as ProjectAdmin should succeed`() {
        val projectId = PROJECT_AOU_OAU_ID
        withUser(OBSERVER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first())
            val result = assignmentTagController.listProjectTimeCategories(projectId).filter { it.name == name2 }.singleOrNull()
            if (result != null) fail()
        }

    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory with User as Observer should fail`() {
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory with User as User should fail`() {
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_OAU_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory with Observer as Observer should fail`() {
        withUser(OBSERVER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_UOA_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory with User and not member should fail`() {
        withUser(USER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory with Observer and not member should fail`() {
        withUser(OBSERVER_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_NO_MEMBERS_IDS.first())
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete timecategory anonymous should fail`() {
        withoutUser {
            assignmentTagController.deleteTimeCategory(TIMECAT_PROJECT_AOU_AUO_IDS.first())
        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete unexisting timecategory should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            assignmentTagController.deleteTimeCategory(PROJECT_NO_MEMBERS_ID)
        }
    }


}
