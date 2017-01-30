package io.ticktag.integrationtests.restinterface.assignmenttag

import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.assignmenttag.controllers.AssignmentTagController
import io.ticktag.restinterface.assignmenttag.schema.CreateAssignmentTagRequestJson
import io.ticktag.restinterface.assignmenttag.schema.UpdateAssignmentRequestJson
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.util.*
import javax.inject.Inject

class AssignmentTagApiTest : ApiBaseTest() {

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Inject lateinit var assignmentTagController: AssignmentTagController

    var assignmentTagId: UUID = UUID.fromString("00000000-0006-0000-0000-000000000104")
    var projectId: UUID = UUID.fromString("00000000-0002-0000-0000-000000000102")
    var name = "New Assignment Tag"
    var color = "00ffff"

    var name2 = "Changed Assignment Tag"
    var color2 = "ffffff"

    val LOCAL_ADMIN_ID: UUID = UUID.fromString("00000000-0001-0000-0000-000000000101")
    val LOCAL_OBSERVER_ID: UUID = UUID.fromString("00000000-0001-0000-0000-000000000102")
    val LOCAL_USER_ID: UUID = UUID.fromString("00000000-0001-0000-0000-000000000103")

    @Test
    fun create_assignmenttag_positive() {
        withUser(LOCAL_USER_ID) { principal ->
            val req = CreateAssignmentTagRequestJson(projectId, name, color)
            assignmentTagController.createAssignmentTag(req)

            assignmentTagController.listAssignmentTags(projectId)

            val result = assignmentTagController.listAssignmentTags(projectId).filter { it.name == name }.singleOrNull()
            if (result != null) {
                assertEquals(result.name, name)
                assertEquals(result.color, color)
            } else {
                fail()
            }

        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun create_assignmenttag_negative() {
        withUser(LOCAL_OBSERVER_ID) { principal ->
            val req = CreateAssignmentTagRequestJson(projectId, name, color)
            assignmentTagController.createAssignmentTag(req)
        }
    }

    @Test
    fun get_assignmenttag_positive() {
        withUser(LOCAL_USER_ID) { principal ->
            val assignmentTag = assignmentTagController.getAssignmentTag(assignmentTagId)

            assertEquals("dev", assignmentTag.name)
            assertEquals("ff0000", assignmentTag.color)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun get_assignmenttag_negative() {
        withoutUser {
            assignmentTagController.getAssignmentTag(assignmentTagId)
        }
    }


    @Test
    fun update_assignmenttag_positive() {
        withUser(LOCAL_USER_ID) { principal ->
            val req = UpdateAssignmentRequestJson(name2, color2)

            val updatedTag = assignmentTagController.updateAssignmentTag(assignmentTagId, req)

            assertEquals(updatedTag.name, name2)
            assertEquals(updatedTag.color, color2)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun update_assignmenttag_negative() {
        withUser(LOCAL_OBSERVER_ID) { principal ->
            val req = UpdateAssignmentRequestJson(name2, color2)
            val updatedTag = assignmentTagController.updateAssignmentTag(assignmentTagId, req)
        }
    }

    @Test
    fun list_assignmenttag_positive() {
        withUser(LOCAL_USER_ID) { principal ->
            val list = assignmentTagController.listAssignmentTags(projectId)
            assertEquals(3, list.size)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun list_assignmenttag_negative() {
        withoutUser {
            assignmentTagController.listAssignmentTags(projectId)
        }
    }

    // TODO
    // No function to search assignmenttags server site
    // Can't @Ignore because the method is actually missingg

    /**
    @Test
    fun search_assignmenttag_positive() {
    withUser(LOCAL_USER_ID) { principal ->
            val list = assignmentTagController.searchAssignmentTags(projectId, "t%")
            assertEquals(list.size, 2)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun search_assignmenttag_negative() {
        withoutUser {
            assignmentTagController.searchAssignmentTags(projectId, "t%")
        }
    }*/
}
