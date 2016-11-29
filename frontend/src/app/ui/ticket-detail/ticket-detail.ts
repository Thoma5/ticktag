import {
  TicketResultJson, CommentResultJson, UserResultJson, TicketTagResultJson,
  AssignmentTagResultJson, TimeCategoryJson, LoggedTimeResultJson, TicketEventResultJson,
  TicketProgressResultJson
} from '../../api';
import * as imm from 'immutable';
import { Tag } from '../../util/taginput/taginput.component';

export class TicketDetailProgress {
  readonly ticketId: string;
  readonly currentEstimatedTime: number;
  readonly totalLoggedTime: number;

  get percent(): number { return this.totalLoggedTime / this.currentEstimatedTime; }

  constructor(ticketId: string, prog: TicketProgressResultJson) {
    this.ticketId = ticketId;
    this.currentEstimatedTime = prog.currentEstimatedTime;
    this.totalLoggedTime = prog.totalLoggedTime;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailProgress.prototype);

export class TicketEvent {
  readonly id: string;
  readonly user: TicketDetailUser;
  readonly time: number;
  readonly rawEvent: TicketEventResultJson;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>) {
    this.id = event.id;
    this.user = users.get(event.userId);  // TODO do something if the user does not exist
    this.time = event.time;
    this.rawEvent = event;
  }
}
Object.freeze(TicketEvent.prototype);

export class TicketEventParentChanged extends TicketEvent {
  readonly srcParent: TicketDetailRelated;
  readonly dstParent: TicketDetailRelated;

  constructor(event: TicketEventResultJson,
              users: imm.Map<string, TicketDetailUser>,
              relatedTickets: imm.Map<string, TicketDetailRelated>) {
    super(event, users);
    let eventParentChanged: any = event;
    this.srcParent = relatedTickets.get(eventParentChanged.srcParentId);
    this.dstParent = relatedTickets.get(eventParentChanged.dstParentId);
    Object.freeze(this);
  }
}
Object.freeze(TicketEventParentChanged.prototype);

export class TicketEventUserAdded extends TicketEvent {
  readonly addedUser: TicketDetailUser;
  readonly assignmentTag: TicketDetailAssTag;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, assTags: imm.Map<string, TicketDetailAssTag>) {
    super(event, users);
    let eventUserChanged: any = event;
    this.addedUser = users.get(eventUserChanged.addedUserId);
    this.assignmentTag = assTags.get(eventUserChanged.assignmentTagId);
    Object.freeze(this);
  }
}
Object.freeze(TicketEventUserAdded.prototype);

export class TicketEventUserRemoved extends TicketEvent {
  readonly removedUser: TicketDetailUser;
  readonly assignmentTag: TicketDetailAssTag;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, assTags: imm.Map<string, TicketDetailAssTag>) {
    super(event, users);
    let eventUserChanged: any = event;
    this.removedUser = users.get(eventUserChanged.removedUserId);
    this.assignmentTag = assTags.get(eventUserChanged.assignmentTagId);
    Object.freeze(this);
  }
}
Object.freeze(TicketEventUserRemoved.prototype);

export class TicketEventLoggedTimeAdded extends TicketEvent {
  readonly category: TicketDetailTimeCategory;
  readonly loggedTime: number;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, cats: imm.Map<string, TicketDetailTimeCategory>) {
    super(event, users);
    let eventLoggedTime: any = event;
    this.category = cats.get(eventLoggedTime.categoryId);
    this.loggedTime = eventLoggedTime.loggedTime;
    Object.freeze(this);
  }
}
Object.freeze(TicketEventLoggedTimeAdded.prototype);

export class TicketEventLoggedTimeRemoved extends TicketEvent {
  readonly category: TicketDetailTimeCategory;
  readonly loggedTime: number;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, cats: imm.Map<string, TicketDetailTimeCategory>) {
    super(event, users);
    let eventLoggedTime: any = event;
    this.category = cats.get(eventLoggedTime.categoryId);
    this.loggedTime = eventLoggedTime.loggedTime;
    Object.freeze(this);
  }
}
Object.freeze(TicketEventLoggedTimeRemoved.prototype);

export class TicketDetailAssignment {
  readonly tag: TicketDetailAssTag;
  readonly transient: boolean;

  constructor(tag: TicketDetailAssTag, transient: boolean) {
    this.tag = tag;
    this.transient = transient;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailAssignment.prototype);

export class TicketEventTagRemoved extends TicketEvent {
  readonly tag: TicketDetailTag;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, tags: imm.Map<string, TicketDetailTag>) {
    super(event, users);
    let eventTag: any = event;
    this.tag = tags.get(eventTag.tagId);
    Object.freeze(this);
  }
}
Object.freeze(TicketEventTagRemoved.prototype);

export class TicketEventTagAdded extends TicketEvent {
  readonly tag: TicketDetailTag;

  constructor(event: TicketEventResultJson, users: imm.Map<string, TicketDetailUser>, tags: imm.Map<string, TicketDetailTag>) {
    super(event, users);
    let eventTag: any = event;
    this.tag = tags.get(eventTag.tagId);
    Object.freeze(this);
  }
}
Object.freeze(TicketEventTagRemoved.prototype);

// TODO remove
export class TicketDetailTransient<T> {
  readonly value: T;
  readonly transient: boolean;

  constructor(value: T, transient: boolean) {
    this.value = value;
    this.transient = transient;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailTransient.prototype);

export class TicketDetailLoggedTime {
  readonly id: string;
  readonly category: TicketDetailTimeCategory;
  readonly time: number;

  constructor(time: LoggedTimeResultJson, cats: imm.Map<string, TicketDetailTimeCategory>) {
    this.id = time.id;
    this.category = cats.get(time.categoryId);  // TODO error handling or dummy time category
    this.time = time.time;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailLoggedTime.prototype);

export class TicketDetailTransientUser {
  readonly user: TicketDetailUser;
  readonly tags: imm.Set<string>;  // This does not need to be a rich object, because all tags are known.

  constructor(user: TicketDetailUser, tags: imm.Set<string>) {
    this.user = user;
    this.tags = tags;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailTransientUser.prototype);

export class TicketDetailTimeCategory {
  readonly id: string;
  readonly normalizedName: string;
  readonly name: string;

  constructor(category: TimeCategoryJson) {
    this.id = category.id;
    this.normalizedName = category.normalizedName;
    this.name = category.name;
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailTimeCategory.prototype);

export class TicketDetailComment {
  readonly id: string;
  readonly createTime: number;
  readonly text: string;
  readonly user: TicketDetailUser;
  readonly loggedTimes: imm.List<TicketDetailLoggedTime>;

  constructor(comment: CommentResultJson, users: imm.Map<string, TicketDetailUser>, times: imm.Map<string, TicketDetailLoggedTime>) {
    this.id = comment.id;
    this.createTime = comment.createTime;
    this.text = comment.text;
    this.user = users.get(comment.userId);  // TODO do something if the user does not exist
    this.loggedTimes = imm.Seq(comment.loggedTimeIds)
      .map(id => times.get(id))
      .filter(it => !!it)
      .toList();
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

export class TicketDetailRelated {
  readonly id: string;
  readonly projectId: string;
  readonly number: number;
  readonly title: string;
  readonly open: boolean;
  readonly currentEstimatedTime: number;
  readonly progress: TicketDetailProgress|undefined;

  constructor(ticket: TicketResultJson, relatedProgresses: imm.Map<string, TicketDetailProgress>) {
    this.id = ticket.id;
    this.projectId = ticket.projectId;
    this.number = ticket.number;
    this.title = ticket.title;
    this.open = ticket.open;
    this.currentEstimatedTime = ticket.currentEstimatedTime;
    this.progress = relatedProgresses.get(this.id);
    Object.freeze(this);
  }
}
Object.freeze(TicketDetailRelated.prototype);

export class TicketDetail {
  readonly comments: imm.List<TicketDetailComment>;
  readonly createTime: number;
  readonly createdBy: TicketDetailUser;
  readonly currentEstimatedTime: number|undefined;
  readonly dueDate: number|undefined;
  readonly description: string;
  readonly id: string;
  readonly initialEstimatedTime: number|undefined;
  readonly number: number;
  readonly open: boolean;
  readonly storyPoints: number|undefined;
  readonly tags: imm.List<TicketDetailTransient<TicketDetailTag>>;
  readonly title: string;
  readonly users: imm.Map<TicketDetailUser, imm.List<TicketDetailAssignment>>;
  readonly projectId: string;
  readonly subtickets: imm.List<TicketDetailRelated>;
  readonly referenced: imm.List<TicketDetailRelated>;
  readonly referencedBy: imm.List<TicketDetailRelated>;
  readonly progress: TicketDetailProgress|undefined;

  constructor(
      ticket: TicketResultJson,
      comments: imm.Map<string, TicketDetailComment>,
      relatedTickets: imm.Map<string, TicketDetailRelated>,
      users: imm.Map<string, TicketDetailUser>,
      ticketTags: imm.Map<string, TicketDetailTag>,
      assignmentTags: imm.Map<string, TicketDetailAssTag>,
      relatedProgresses: imm.Map<string, TicketDetailProgress>,
      transientUsers: imm.List<TicketDetailTransientUser>,
      transientTags: imm.Set<string>,
      transientTicket: TicketDetailTransientFields,
      progress: TicketDetailProgress,
      ) {
    this.comments = imm.List(ticket.commentIds)
      .map(cid => comments.get(cid))
      .filter(it => !!it)
      .toList();
    this.createTime = ticket.createTime;
    this.createdBy = users.get(ticket.createdBy);  // TODO error handling or create a dummy user
    this.currentEstimatedTime = coalesce(transientTicket.currentEstimatedTime, ticket.currentEstimatedTime);
    this.dueDate = coalesce(transientTicket.dueDate, ticket.dueDate);
    this.description = coalesce(transientTicket.description, ticket.description);
    this.id = ticket.id;
    this.initialEstimatedTime = coalesce(transientTicket.initialEstimatedTime, ticket.initialEstimatedTime);
    this.number = ticket.number;
    this.open = ticket.open;
    this.storyPoints = coalesce(transientTicket.storyPoints, ticket.storyPoints);
    this.progress = progress;
    this.tags = imm.List(ticket.tagIds)
      .map(tid => new TicketDetailTransient(ticketTags.get(tid), false))
      .filter(it => !!it.value)
      .toList()
      .withMutations(tags => {
        transientTags.forEach(transientTag => {
          let i = tags.findIndex(tag => tag.value.id === transientTag);
          if (i >= 0) {
            let old = tags.get(i);
            tags.set(i, new TicketDetailTransient(old.value, true));
          } else {
            let tag = ticketTags.get(transientTag);
            if (tag) {
              tags.push(new TicketDetailTransient(tag, true));
            }
          }
        });
      });
    this.title = coalesce(transientTicket.title, ticket.title);
    this.users = imm.List(ticket.ticketUserRelations)
      .map(as => ({ user: users.get(as.userId), tag: assignmentTags.get(as.assignmentTagId) }))
      .filter(as => !!as.user && !!as.tag)
      .groupBy(as => as.user)
      .map(entry => entry.map(as => new TicketDetailAssignment(as.tag, false)).toList())
      .toMap()
      .withMutations(us => {
        transientUsers.forEach(transientUser => {
          let key = us.findKey((_, k) => k.id === transientUser.user.id);
          if (!key) {
            key = transientUser.user;
            us.set(key, imm.List<TicketDetailAssignment>());
          }
          us.set(key, us.get(key).withMutations((assignments) => {
            transientUser.tags.forEach((tagId) => {
              let i = assignments.findIndex(ass => ass.tag.id === tagId);
              if (i >= 0) {
                let old = assignments.get(i);
                assignments.set(i, new TicketDetailAssignment(old.tag, true));
              } else {
                let tag = assignmentTags.get(tagId);
                if (tag) {
                  assignments.push(new TicketDetailAssignment(tag, true));
                }
              }
            });
          }));
        });
      });
    this.projectId = ticket.projectId;
    this.subtickets = imm.Seq(ticket.subTicketIds)
      .map(id => relatedTickets.get(id))
      .filter(t => !!t)
      .toList();
    this.referenced = imm.Seq(ticket.referencedTicketIds)
      .map(id => relatedTickets.get(id))
      .filter(t => !!t)
      .toList();
    this.referencedBy = imm.Seq(ticket.referencingTicketIds)
      .map(id => relatedTickets.get(id))
      .filter(t => !!t)
      .toList();
    this.progress = relatedProgresses.get(this.id);
    Object.freeze(this);
  }
}
Object.freeze(TicketDetail.prototype);

export interface TicketDetailTransientFields {
  title?: string;
  storyPoints?: number;
  initialEstimatedTime?: number;
  currentEstimatedTime?: number;
  dueDate?: number;
  description?: string;
}

// Do not export this function. It works only with undefined and is specific
// to the usecase here.
function coalesce<T>(first: T|undefined, second: T): T {
  if (first === undefined) {
    return second;
  } else {
    return first;
  }
}
