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
    @GetMapping(value = "{pID}/member/{uID}")
    open fun getMember(
            @PathVariable uID: UUID,
            @PathVariable pID: UUID): MemberResultJson {
        val member = memberService.getMember(uID, pID)
        return MemberResultJson(member)
    }

    @PostMapping(value = "{pID}/member/{uID}")
    open fun createMember(
            @PathVariable uID: UUID,
            @PathVariable pID: UUID,
            @RequestBody req: CreateMemberRequestJson): MemberResultJson {
        val member = memberService.createMember(uID, pID, CreateMember(req.projectRole))
        return MemberResultJson(member)
    }

    @DeleteMapping(value = "{pID}/member/{uID}")
    open fun deleteMember(
            @PathVariable uID: UUID,
            @PathVariable pID: UUID) {
        memberService.deleteMember(uID, pID)
    }

    @PutMapping(value = "{pID}/member/{uID}")
    open fun updateMember(
            @PathVariable uID: UUID,
            @PathVariable pID: UUID,
            @RequestBody req: UpdateMemberRequestJson): MemberResultJson {
        val member = memberService.updateMember(uID, pID, UpdateMember(req.projectRole))
        return MemberResultJson(member)
    }
}