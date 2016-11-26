import {
  TicketResultJson, CommentResultJson, UserResultJson, TicketTagResultJson,
  AssignmentTagResultJson, TimeCategoryJson
} from '../../api';
import * as imm from 'immutable';
import { Tag } from '../../util/taginput/taginput.component';

export class TicketDetailTimeCategory {
  readonly id: string;
  readonly normalizedName: string;
  readonly name: string;

  constructor(category: TimeCategoryJson) {
    this.id = category.id;
    this.normalizedName = category.normalizedName;
    this.name = name;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailTimeCategory.prototype);

export class TicketDetailComment {
  readonly id: string;
  readonly createTime: number;
  readonly text: string;
  readonly user: TicketDetailUser;

  constructor(comment: CommentResultJson, users: imm.Map<string, TicketDetailUser>) {
    this.id = comment.id;
    this.createTime = comment.createTime;
    this.text = comment.text;
    this.user = users.get(comment.userId);  // TODO do something if the user does not exist
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailComment.prototype);

export class TicketDetailUser {
  readonly id: string;
  readonly mail: string;
  readonly name: string;
  readonly username: string;

  constructor(user: UserResultJson) {
    this.id = user.id;
    this.mail = user.mail;
    this.name = user.name;
    this.username = user.username;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailUser.prototype);

export class TicketDetailTag implements Tag {
  readonly id: string;
  readonly name: string;
  readonly normalizedName: string;
  readonly order: number;
  readonly color: string;

  constructor(tag: TicketTagResultJson) {
    this.id = tag.id;
    this.name = tag.name;
    this.normalizedName = tag.normalizedName;
    this.order = tag.order;
    this.color = tag.color;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailTag.prototype);

export class TicketDetailAssTag implements Tag {
  readonly id: string;
  readonly name: string;
  readonly normalizedName: string;
  readonly order: number;
  readonly color: string;

  constructor(tag: AssignmentTagResultJson, order: number) {
    this.id = tag.id;
    this.name = tag.name;
    this.normalizedName = tag.normalizedName;
    this.order = order;
    this.color = tag.color;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailAssTag.prototype);

export class TicketDetail {
  readonly comments: imm.List<TicketDetailComment>;
  readonly createTime: number;  // TODO convert
  readonly createdBy: TicketDetailUser;
  readonly currentEstimatedTime: number|undefined;  // TODO convert
  readonly dueDate: number|undefined;  // TODO convert
  readonly description: string;
  readonly id: string;
  readonly initialEstimatedTime: number|undefined;  // TODO convert
  readonly number: number;
  readonly open: boolean;
  readonly storyPoints: number|undefined;
  readonly tags: imm.List<TicketDetailTag>;
  readonly title: string;
  readonly users: imm.Map<TicketDetailUser, imm.List<TicketDetailAssTag>>;
  readonly projectId: string;

  constructor(
      ticket: TicketResultJson,
      comments: imm.Map<string, TicketDetailComment>,
      users: imm.Map<string, TicketDetailUser>,
      ticketTags: imm.Map<string, TicketDetailTag>,
      assignmentTags: imm.Map<string, TicketDetailAssTag>,
      transientUsers: imm.Set<string>) {
    this.comments = imm.List(ticket.commentIds)
      .map(cid => comments.get(cid))
      .filter(it => !!it)
      .toList();
    this.createTime = ticket.createTime;
    this.createdBy = users.get(ticket.createdBy);  // TODO error handling or create a dummy user
    this.currentEstimatedTime = ticket.currentEstimatedTime;
    this.dueDate = ticket.dueDate;
    this.description = ticket.description;
    this.id = ticket.id;
    this.initialEstimatedTime = ticket.initialEstimatedTime;
    this.number = ticket.number;
    this.open = ticket.open;
    this.storyPoints = ticket.storyPoints;
    this.tags = imm.List(ticket.tagIds)
      .map(tid => ticketTags.get(tid))
      .filter(it => !!it)
      .toList();
    this.title = ticket.title;
    this.users = imm.List(ticket.ticketAssignments)
      .map(as => ({ user: users.get(as.userId), tag: assignmentTags.get(as.assignmentTagId) }))
      .filter(as => !!as.user && !!as.tag)
      .groupBy(as => as.user)
      .map(entry => entry.map(as => as.tag).toList())
      .toMap()
      .withMutations(us => {
        transientUsers.forEach(userId => {
          if (users.has(userId)) {
            us.set(users.get(userId), imm.List<TicketDetailAssTag>());
          }
        });
      });
    this.projectId = ticket.projectId;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetail.prototype);
