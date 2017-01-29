#!/usr/bin/env python3
import random, re, bcrypt
from datetime import datetime, timedelta
from uuid import UUID
from faker import Factory

USER_COUNT = 50
TICKET_COUNT = 500
PASSWORD_HASH = bcrypt.hashpw(b"password", bcrypt.gensalt(prefix=b"2a")).decode("ASCII")
TICKET_TAGS = (
    ((("Bug", "c23b22"), ("Feature", "77dd77"), ("Idea", "fdfd96")), 0.9, "Type"),
    ((("Critical", "c23b22"), ("High", "ffb347"), ("Low", "aec6cf")), 0.25, "Severity"),
    ((("Not Planned", "e5dcdc"), ("Backlog", "bbbbbb"), ("Sprint 1", "bbbbbb"), ("Sprint 2", "bbbbbb"), ("Sprint 3", "bbbbbb"), ("Sprint 4", "bbbbbb")), 1, "Sprint"),
    ((("Regression", "ffd1dc"),), 0.01, "Regression"),
    ((("Easy", "779ecb"), ("Hard", "c23b22")), 0.1, "Difficulty"),
    ((("UI", "b9cc5b"), ("Frontend", "b9cc5b"), ("Backend", "b9cc5b"), ("Database", "b9cc5b")), 0.7, "Layer"),
)
ASSIGNMENT_TAGS = (
    ("Ticket Owner", (0.8,), "b19cd9"),
    ("Implementer", (0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7), "fdfd96"),
    ("Reviewer", (0.7, 0.5, 0.3, 0.1), "aec6cf"),
    ("Domain Expert", (0.3, 0.9, 0.8, 0.1), "77dd77"),
    ("Blocked", (0.01, 0.5, 0.5), "ff6961"),
)
TIME_CATS = ("Implementing", "Reviewing", "Meeting", "Planning")
ROLES = ["OBSERVER","ADMIN","USER"]
PROJECT_ROLES = ["OBSERVER","ADMIN","NONE", "USER"]

def random_datetime(may_none):
    if may_none and random.random() < 0.1:
        return None
    else:
        return datetime(year=2016, month=12, day=10, hour=15) - timedelta(hours=random.randint(1, 365*24))

def random_comment():
    return '\n\n'.join(faker.paragraphs(nb=random.randint(1, 10)))

def random_title():
    return " ".join((faker.word() for i in range(random.randint(2, 5))))

def comment_count():
    return random.randint(0, 20)

def subticket_count():
    if random.random() < 0.5:
        return 0
    else:
        return random.randint(1, 10)

def event_count():
    return random.randint(0, 20)

def reference_count():
    return random.randint(0, 10)

def random_est_time():
    if random.random() < 0.1:
        return None
    else:
        return random.randint(3600000000000, 360000000000000)  # 1h to 100h

def random_sp():
    if random.random() < 0.1:
        return None
    else:
        return random.randint(1, 64)

def random_status():
    return random.random() < 0.8

def random_logged_time(time_left=None, may_none=True):
    if (not may_none) or ((time_left is None or time_left >= 60000000000) and random.random() < 0.05):
        if time_left is None:
            time_left = 360000000000000
        return random.randint(60000000000, time_left)
    else:
        return None

def random_uuid():
    return UUID(int=random.randint(0, 2**128-1))

faker = Factory.create('de_AT')
faker.random.seed(2017, version=2)
random.seed(2017, version=2)

def sql(value):
    if value is None:
        return "NULL"
    elif isinstance(value, str):
        return "'" + value + "'"
    elif isinstance(value, UUID):
        return "'" + str(value) + "'"
    elif isinstance(value, datetime):
        return "'" + str(value) + "'"
    else:
        return repr(value)

class User:
    def __init__(self, usercount):
        self.id = random_uuid()
        self.name = faker.name()
        self.username = re.sub(r"[^a-z0-9_]", "", self.name.lower())
        self.mail = re.sub(r"[^a-z0-9_]", "", self.name.lower()) + "@example.invalid"
        self.password_hash = PASSWORD_HASH
        self.role = ROLES[min(2, usercount %10)]
        self.current_token = random_uuid()
        self.disabled = usercount % 15 == 0

    def insert(self):
        return """insert into public.user values ({}, {}, {}, {}, {}, {}, {}, {});""".format(
                sql(self.id),
                sql(self.username),
                sql(self.mail),
                sql(self.name),
                sql(self.password_hash),
                sql(self.role),
                sql(self.current_token),
                sql(self.disabled))

class Project:
    def __init__(self):
        self.id = random_uuid()
        self.name = faker.color_name()[:21] + " Project"
        self.description = faker.catch_phrase()
        self.time = random_datetime(False)
        self.disabled = False
    
    def insert(self):
        return """
            insert into public.project
            values ({}, {}, {}, {}, {}, {}, {})
        ;""".format(
            sql(self.id),
            sql(self.name),
            sql(self.description),
            sql(self.time),
            sql(None),
            sql(None),
            sql(self.disabled))

class TicketTag:
    def __init__(self, group, tag, order):
        self.id = random_uuid()
        self.ticket_tag_group = group
        self.name = tag[0]
        self.color = tag[1]
        self.order = order
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
    
    def insert(self):
        return """
            insert into public.ticket_tag
            values ({}, {}, {}, {}, {}, {})
        ;""".format(
            sql(self.id),
            sql(self.ticket_tag_group.id),
            sql(self.name),
            sql(self.normalized_name),
            sql(self.color),
            sql(self.order),
            sql(False),
            sql(False))

class AssignmentTag:
    def __init__(self, project, tag):
        self.id = random_uuid()
        self.project = project
        self.name = tag[0]
        self.color = tag[2]
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
        self.chances = tag[1]
    
    def insert(self):
        return """
            insert into public.assignment_tag
            values ({}, {}, {}, {}, {})
        ;""".format(
            sql(self.id),
            sql(self.project.id),
            sql(self.name),
            sql(self.normalized_name),
            sql(self.color))

class TimeCat:
    def __init__(self, project, cat):
        self.id = random_uuid()
        self.project = project
        self.name = cat
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
    
    def insert(self):
        return """insert into time_category values ({}, {}, {}, {});\n\r""".format(
            sql(self.id),
            sql(self.project.id),
            sql(self.name),
            sql(self.normalized_name))

class TicketTagGroup:
    def __init__(self, project, order, group):
        self.id = random_uuid()
        self.project = project
        self.tags = [TicketTag(self, tag, order*100+i) for i, tag in enumerate(group[0])]
        self.default_ticket_tag = self.tags[0] if group[1] == 1 else None
        self.exclusive = group[1] == 1
        self.name = group[2]
        self.chance = group[1]

    def insert(self):
        s = """insert into ticket_tag_group values ({}, {}, {}, {}, {});\n\r""".format(
            sql(self.id),
            sql(self.project.id),
            sql(None),
            sql(self.name),
            sql(self.exclusive))
        s += "\n".join((t.insert() for t in self.tags))
        if self.default_ticket_tag:
            s += "\nupdate ticket_tag_group set default_ticket_tag_id = {} where id = {};".format(sql(self.default_ticket_tag.id), sql(self.id))
        return s

class Comment:
    def __init__(self, ticket, user):
        self.id = random_uuid()
        self.user = user
        self.ticket = ticket
        self.time = random_datetime(False)
        self.text = random_comment()

    def insert(self):
        return """ insert into comment values ({}, {}, {}, {}, {});\n\r""".format(
            sql(self.id),
            sql(self.user.id),
            sql(self.ticket.id),
            sql(self.time),
            sql(self.text))

class Ticket:
    def __init__(self, project, users, number, parent):
        self.id = random_uuid()
        self.number = number
        self.project = project
        self.created_by = random.choice(users)
        self.title = random_title()
        self.open = random_status()
        self.story_points = random_sp()
        self.description_comment = Comment(self, self.created_by)
        self.parent = parent
        self.initial_est = random_est_time()
        self.current_est = self.initial_est
        self.comments = [self.description_comment]
        self.due_date = random_datetime(True)
        self.create_time = random_datetime(False)
    
    def insert(self):
        s = """
            insert into ticket
            values ({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
        ;""".format(
            sql(self.id),
            sql(self.number),
            sql(self.parent.id if self.parent else None),
            sql(self.project.id),
            sql(self.created_by.id),
            sql(None),
            sql(self.create_time),
            sql(self.title),
            sql(self.open),
            sql(self.story_points),
            sql(self.initial_est),
            sql(self.current_est),
            sql(self.due_date))
        s += self.description_comment.insert()
        s += "\nupdate ticket set description_comment_id = {} where id = {};".format(sql(self.description_comment.id), sql(self.id))
        return s

def create_ticket(project, users, time_cats, tag_groups, assignment_tags, i, parent):
    ticket = Ticket(project, users, i, parent)
    print(ticket.insert())

    time_left = ticket.current_est

    for group in tag_groups:
        if random.random() <= group.chance:
            print("insert into assigned_ticket_tag values ({}, {});".format(
                sql(ticket.id),
                sql(random.choice(group.tags).id)))

    for tag in assignment_tags:
        this_users = random.sample(users, k=len(users))
        this_users_at = 0
        for chance in tag.chances:
            if random.random() <= chance:
                print("insert into assigned_ticket_user values({}, {}, {});".format(
                    sql(ticket.id),
                    sql(tag.id),
                    sql(this_users[this_users_at].id)))
                this_users_at += 1
            else:
                break
                
    for j in range(comment_count()):
        comment = Comment(ticket, random.choice(users))
        ticket.comments.append(comment)
        print(comment.insert())

        time = random_logged_time(time_left)
        while time:
            print("insert into logged_time values ({}, {}, {}, {}, {});".format(
                sql(random_uuid()),
                sql(comment.id),
                sql(random.choice(time_cats).id),
                sql(time),
                sql(False)))
            if not (time_left is None):
                time_left -= time
            time = random_logged_time(time_left)

    return ticket

def create_event(users, tickets, tag_groups, assignment_tags, time_cats, ticket):
    id = random_uuid()
    user = random.choice(users)

    print("insert into ticket_event values ({}, {}, {}, {});".format(
        sql(id),
        sql(ticket.id),
        sql(user.id),
        sql(random_datetime(False))))

    def comment_text_changed():
        print("insert into ticket_event_comment_text_changed values ({}, {}, {}, {});".format(
            sql(id),
            sql(random.choice(ticket.comments).id),
            sql(random_comment()),
            sql(random_comment())))
    def current_est_time_changed():
        print("insert into ticket_event_current_estimated_time_changed values ({}, {}, {});".format(
            sql(id),
            sql(random_est_time()),
            sql(random_est_time())))
    def due_date_changed():
        print("insert into ticket_event_due_date_changed values ({}, {}, {});".format(
            sql(id),
            sql(random_datetime(True)),
            sql(random_datetime(True))))
    def initial_est_time_changed():
        print("insert into ticket_event_initial_estimated_time_changed values ({}, {}, {});".format(
            sql(id),
            sql(random_est_time()),
            sql(random_est_time())))
    def logged_time_added():
            print("insert into ticket_event_logged_time_added values ({}, {}, {}, {});".format(
                sql(id),
                sql(random.choice(ticket.comments).id),
                sql(random.choice(time_cats).id),
                sql(random_logged_time(None, False))))
    def logged_time_removed():
            print("insert into ticket_event_logged_time_removed values ({}, {}, {}, {});".format(
                sql(id),
                sql(random.choice(ticket.comments).id),
                sql(random.choice(time_cats).id),
                sql(random_logged_time(None, False))))
    def mention_added():
        print("insert into ticket_event_mention_added values ({}, {}, {});".format(
            sql(id),
            sql(random.choice(ticket.comments).id),
            sql(random.choice(tickets).id)))
    def mention_removed():
        print("insert into ticket_event_mention_removed values ({}, {}, {});".format(
            sql(id),
            sql(random.choice(ticket.comments).id),
            sql(random.choice(tickets).id)))
    def parent_changed():
        print("insert into ticket_event_parent_changed values ({}, {}, {});".format(
            sql(id),
            sql(random.choice(tickets).id),
            sql(random.choice(tickets).id)))
    def state_changed():
        new_state = False;
        print("insert into ticket_event_state_changed values({}, {}, {});".format(
            sql(id),
            sql(not new_state),
            sql(new_state)))
    def story_points_changed():
        print("insert into ticket_event_story_points_changed values({}, {}, {});".format(
            sql(id),
            sql(random_sp()),
            sql(random_sp())))
    def tag_added():
        print("insert into ticket_event_tag_added values ({}, {});".format(
            sql(id),
            sql(random.choice(random.choice(tag_groups).tags).id)))
    def tag_removed():
        print("insert into ticket_event_tag_removed values ({}, {});".format(
            sql(id),
            sql(random.choice(random.choice(tag_groups).tags).id)))
    def title_changed():
        print("insert into ticket_event_title_changed values({}, {}, {});".format(
            sql(id),
            sql(random_title()),
            sql(random_title())))
    def user_added():
        print("insert into ticket_event_user_added values ({}, {}, {});".format(
            sql(id),
            sql(random.choice(users).id),
            sql(random.choice(assignment_tags).id)))
    def user_removed():
        print("insert into ticket_event_user_removed values ({}, {}, {});".format(
            sql(id),
            sql(random.choice(users).id),
            sql(random.choice(assignment_tags).id)))

    random.choice([
        comment_text_changed, current_est_time_changed, due_date_changed,
        initial_est_time_changed, logged_time_added, logged_time_removed,
        mention_added, mention_removed, parent_changed, state_changed,state_changed,state_changed,state_changed,state_changed,state_changed,
        story_points_changed, tag_added, tag_removed, title_changed,
        user_added, user_removed,
    ])()

def main():
    print("set client_encoding to 'utf8';")
    print("begin;")
    general_count = 0
    for seed in range(10):
        users = [User(i) for i in range(USER_COUNT)]
        for user in users:
            print(user.insert())
        
        project = Project()
        print(project.insert())
        p_user = 0;
        for user in users:
            print("insert into member values ({}, {}, {}, {});".format(
                sql(user.id),
                sql(project.id),
                sql(PROJECT_ROLES[min(3, p_user %10)]),
                sql(random_datetime(False))))
            p_user += 1
        
        tag_groups = [TicketTagGroup(project, i, group) for i, group in enumerate(TICKET_TAGS)]
        for group in tag_groups:
            print(group.insert())

        assignment_tags = [AssignmentTag(project, tag) for tag in ASSIGNMENT_TAGS]
        for tag in assignment_tags:
            print(tag.insert())

        time_cats = [TimeCat(project, cat) for cat in TIME_CATS]
        for cat in time_cats:
            print(cat.insert())

        tickets = []
        ticket_number = 1
        for i in range(TICKET_COUNT):
            ticket = create_ticket(project, users, time_cats, tag_groups, assignment_tags, ticket_number, None)
            tickets.append(ticket)
            ticket_number += 1

            for j in range(subticket_count()):
                subticket = create_ticket(project, users, time_cats, tag_groups, assignment_tags, ticket_number, ticket)
                tickets.append(subticket)
                ticket_number += 1

        for ticket in tickets:
            for i in range(event_count()):
                create_event(users, tickets, tag_groups, assignment_tags, time_cats, ticket)

            inserted = set()
            for i in range(reference_count()):
                comment_id = random.choice(ticket.comments).id
                if comment_id not in inserted:
                    print("insert into mentioned_ticket values ({}, {});".format(
                        sql(comment_id),
                        sql(random.choice(tickets).id)))
                    inserted.add(comment_id)

    print("commit;")
    print("vacuum full freeze analyze;")

if __name__ == "__main__":
    main()
