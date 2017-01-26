package io.ticktag.restinterface.member.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.member.schema.CreateMemberRequestJson
import io.ticktag.restinterface.member.schema.MemberResultJson
import io.ticktag.restinterface.member.schema.UpdateMemberRequestJson
import io.ticktag.service.member.MemberService
import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.UpdateMember
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/project")
@Api(tags = arrayOf("member"), description = "project member management")
open class MemberController @Inject constructor(
        private val memberService: MemberService
) {
    @GetMapping(value = "{projectId}/member/{userId}")
    open fun getMember(
            @PathVariable userId: UUID,
            @PathVariable projectId: UUID): MemberResultJson {
        val member = memberService.getMember(userId, projectId)
        return MemberResultJson(member)
    }

    @PostMapping(value = "{projectId}/member/{userId}")
    open fun createMember(
            @PathVariable userId: UUID,
            @PathVariable projectId: UUID,
            @RequestBody req: CreateMemberRequestJson): MemberResultJson {
        val member = memberService.createMember(userId, projectId, CreateMember(req.projectRole,req.defaultAssignmentTagId))
        return MemberResultJson(member)
    }

    @DeleteMapping(value = "{projectId}/member/{userId}")
    open fun deleteMember(
            @PathVariable userId: UUID,
            @PathVariable projectId: UUID) {
        memberService.deleteMember(userId, projectId)
    }

    @PutMapping(value = "{projectId}/member/{userId}")
    open fun updateMember(
            @PathVariable userId: UUID,
            @PathVariable projectId: UUID,
            @RequestBody req: UpdateMemberRequestJson): MemberResultJson {
        val member = memberService.updateMember(userId, projectId, UpdateMember(req.projectRole,req.defaultAssignmentTagId))
        return MemberResultJson(member)
    }
}