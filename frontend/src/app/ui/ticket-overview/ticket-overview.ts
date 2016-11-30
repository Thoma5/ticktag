import {
  TicketResultJson, UserResultJson, TicketTagResultJson,
  AssignmentTagResultJson
} from '../../api';
import * as imm from 'immutable';
import { Tag } from '../../util/taginput/taginput.component';

export class TicketOverviewAssignment {
  readonly tag: TicketOverviewAssTag;

  constructor(tag: TicketOverviewAssTag) {
    this.tag = tag;
    Object.freeze(this);
  }
}
Object.freeze(TicketOverviewAssignment.prototype);


export class TicketOverviewUser {
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
Object.freeze(TicketOverviewUser.prototype);

export class TicketOverviewTag implements Tag {
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
Object.freeze(TicketOverviewTag.prototype);

export class TicketOverviewAssTag implements Tag {
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


export class TicketOverview {
  readonly createTime: number;
  readonly createdBy: TicketOverviewUser;
  readonly currentEstimatedTime: number|undefined;
  readonly dueDate: number|undefined;
  readonly description: string;
  readonly id: string;
  readonly initialEstimatedTime: number|undefined;
  readonly number: number;
  readonly open: boolean;
  readonly storyPoints: number|undefined;
  readonly tags: imm.List<TicketOverviewTag>;
  readonly title: string;
  readonly users: imm.Map<TicketOverviewUser, imm.List<TicketOverviewAssignment>>;
  readonly projectId: string;

  constructor(
      ticket: TicketResultJson,
      users: imm.Map<string, TicketOverviewUser>,
      ticketTags: imm.Map<string, TicketOverviewTag>,
      assignmentTags: imm.Map<string, TicketOverviewAssTag>) {
    this.createTime = ticket.createTime;
    this.currentEstimatedTime = ticket.currentEstimatedTime;
    this.dueDate = ticket.dueDate;
    this.description = ticket.description;
    this.id = ticket.id;
    this.initialEstimatedTime = ticket.initialEstimatedTime;
    this.number = ticket.number;
    this.open = ticket.open;
    this.storyPoints = ticket.storyPoints;
    this.tags =  imm.List(ticket.tagIds)
    .map(tid => ticketTags.get(tid))
    .sort((a, b) => (a.order < b.order) ? -1 : (a.order === b.order ? 0 : 1))
    .toList();
    this.title = ticket.title;
    this.users = imm.List(ticket.ticketUserRelations)
      .map(as => ({ user: users.get(as.userId), tag: assignmentTags.get(as.assignmentTagId) }))
      .filter(as => !!as.user && !!as.tag)
      .groupBy(as => as.user)
      .map(entry => entry.map(as => new TicketOverviewAssignment(as.tag)).toList())
      .toMap();
    this.projectId = ticket.projectId;
  }
}
Object.freeze(TicketOverview.prototype);
