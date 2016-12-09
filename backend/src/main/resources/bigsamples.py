#!/usr/bin/env python3
import random, re, bcrypt
from datetime import datetime
from uuid import uuid4
from faker import Factory

USER_COUNT = 100
TICKET_COUNT = 1000
PASSWORD_HASH = bcrypt.hashpw(b"password", bcrypt.gensalt(prefix=b"2a")).decode("ASCII")
TICKET_TAGS = (
    (("Bug", "Feature", "Idea"), 0.9, "Type"),
    (("Critical", "High", "Low"), 0.25, "Severity"),
    (("Not Planned", "Backlog", "Sprint 1", "Sprint 2", "Sprint 3", "Sprint 4"), 1, "Sprint"),
    (("Regression",), 0.01, "Regression"),
    (("Easy", "Hard"), 0.1, "Difficulty"),
    (("UI", "Frontend", "Backend", "Database"), 0.7, "Layer"),
)
ASSIGNMENT_TAGS = (
    ("Ticket Owner", (0.8,)),
    ("Implementer", (0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7, 0.7)),
    ("Reviewer", (0.7, 0.5, 0.3, 0.1)),
    ("Domain Expert", (0.3, 0.9, 0.8, 0.1)),
    ("Blocked", (0.01, 0.5, 0.5)),
)
TIME_CATS = ("Implementing", "Reviewing", "Meeting", "Planning")

def random_comment():
    return '\n\n'.join(faker.paragraphs(nb=random.randint(1, 10)))

def random_title():
    return " ".join((faker.word() for i in range(random.randint(2, 5))))

def comment_count():
    return random.randint(0, 200)

def subticket_count():
    if random.random() < 0.5:
        return 0
    else:
        return random.randint(1, 10)

def random_est_time():
    if random.random() < 0.1:
        return None
    else:
        return random.randint(3600000000000, 360000000000000)  # 1h to 100h

def random_sp():
    if random.random() < 0.1:
        return None
    else:
        return random.randint(1, 128)

def random_status():
    return random.random() < 0.8

def random_logged_time(time_left):
    if (time_left is None or time_left >= 60000000000) and random.random() < 0.1:
        if time_left is None:
            time_left = 360000000000000
        return random.randint(60000000000, time_left)
    else:
        return None

faker = Factory.create('de_AT')
faker.seed(2017)

class User:
    def __init__(self):
        self.id = uuid4()
        self.name = faker.name()
        self.username = re.sub(r"[^a-z0-9_]", "", self.name.lower())
        self.mail = re.sub(r"[^a-z0-9_]", "", self.name.lower()) + "@example.invalid"
        self.password_hash = PASSWORD_HASH
        self.role = "USER"
        self.current_token = uuid4()

    def insert(self):
        return """
            insert into public.user
            values ('{0.id}', '{0.username}', '{0.mail}', '{0.name}', '{0.password_hash}', '{0.role}', '{0.current_token}')
            ;""".format(self)

class Project:
    def __init__(self):
        self.id = uuid4()
        self.name = "Big Project"
        self.description = faker.sentence()
    
    def insert(self):
        return """
            insert into public.project
            values ('{0.id}', '{0.name}', '{0.description}', now(), NULL)
        ;""".format(self)

class TicketTag:
    def __init__(self, group, name, order):
        self.id = uuid4()
        self.ticket_tag_group = group
        self.name = name
        self.color = "ffffaa"
        self.order = order
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
    
    def insert(self):
        return """
            insert into public.ticket_tag
            values ('{0.id}', '{0.ticket_tag_group.id}', '{0.name}', '{0.normalized_name}', '{0.color}', {0.order})
        ;""".format(self)

class AssignmentTag:
    def __init__(self, project, tag):
        self.id = uuid4()
        self.project = project
        self.name = tag[0]
        self.color = "ffffaa"
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
        self.chances = tag[1]
    
    def insert(self):
        return """
            insert into public.assignment_tag
            values ('{0.id}', '{0.project.id}', '{0.name}', '{0.normalized_name}', '{0.color}')
        ;""".format(self)

class TimeCat:
    def __init__(self, project, cat):
        self.id = uuid4()
        self.project = project
        self.name = cat
        self.normalized_name = re.sub(r"[^a-z0-9_]", "", self.name.lower())
    
    def insert(self):
        return """
            insert into time_category
            values ('{0.id}', '{0.project.id}', '{0.name}', '{0.normalized_name}')
        ;""".format(self)

class TicketTagGroup:
    def __init__(self, project, order, group):
        self.id = uuid4()
        self.project = project
        self.tags = [TicketTag(self, tag, order*100+i) for i, tag in enumerate(group[0])]
        self.default_ticket_tag = self.tags[0] if group[1] == 1 else None
        self.exclusive = group[1] == 1
        self.name = group[2]
        self.chance = group[1]

    def insert(self):
        s = """
            insert into ticket_tag_group
            values ('{0.id}', '{0.project.id}', null, '{0.name}', {0.exclusive})
        ;\n""".format(self)
        s += "\n".join((t.insert() for t in self.tags))
        if self.default_ticket_tag:
            s += "\nupdate ticket_tag_group set default_ticket_tag_id = '{}' where id = '{}';".format(self.default_ticket_tag.id, self.id)
        return s

class Comment:
    def __init__(self, ticket, user):
        self.id = uuid4()
        self.user = user
        self.ticket = ticket
        self.text = random_comment()

    def insert(self):
        return """
            insert into comment
            values ('{0.id}', '{0.user.id}', '{0.ticket.id}', now(), '{0.text}')
        ;""".format(self)

class Ticket:
    def __init__(self, project, users, number, parent):
        self.id = uuid4()
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
    
    def insert(self):
        s = """
            insert into ticket
            values ('{0.id}', {0.number}, {1}, '{0.project.id}', '{0.created_by.id}',
                    NULL, now(), '{0.title}', {0.open}, {4},
                    {2}, {3}, NULL)
        ;""".format(
            self,
            "'{}'".format(self.parent.id) if self.parent else "null",
            str(self.initial_est) if self.initial_est else "null",
            str(self.current_est) if self.current_est else "null",
            str(self.story_points) if self.story_points else "null")
        s += self.description_comment.insert()
        s += "\nupdate ticket set description_comment_id = '{}' where id = '{}';".format(self.description_comment.id, self.id)
        return s

def create_ticket(project, users, time_cats, tag_groups, assignment_tags, i, parent):
    ticket = Ticket(project, users, i, parent)
    print(ticket.insert())

    time_left = ticket.current_est

    for group in tag_groups:
        if random.random() <= group.chance:
            print("insert into assigned_ticket_tag values ('{}', '{}');".format(
                ticket.id,
                random.choice(group.tags).id))

    for tag in assignment_tags:
        this_users = random.sample(users, k=len(users))
        this_users_at = 0
        for chance in tag.chances:
            if random.random() <= chance:
                print("insert into assigned_ticket_user values('{}', '{}', '{}');".format(
                    ticket.id, tag.id, this_users[this_users_at].id))
                this_users_at += 1
            else:
                break
                
    for j in range(comment_count()):
        comment = Comment(ticket, random.choice(users))
        print(comment.insert())

        time = random_logged_time(time_left)
        while time:
            print("insert into logged_time values ('{}', '{}', '{}', {});".format(
                uuid4(),
                comment.id,
                random.choice(time_cats).id,
                time));
            if not (time_left is None):
                time_left -= time
            time = random_logged_time(time_left)

    return ticket

def main():
    print("set client_encoding to 'utf8';")
    print("begin;")

    users = [User() for i in range(USER_COUNT)]
    for user in users:
        print(user.insert())
    
    project = Project()
    print(project.insert())

    for user in users:
        print("insert into member values ('{}', '{}', 'USER', now());".format(user.id, project.id))
    
    tag_groups = [TicketTagGroup(project, i, group) for i, group in enumerate(TICKET_TAGS)]
    for group in tag_groups:
        print(group.insert())

    assignment_tags = [AssignmentTag(project, tag) for tag in ASSIGNMENT_TAGS]
    for tag in assignment_tags:
        print(tag.insert())

    time_cats = [TimeCat(project, cat) for cat in TIME_CATS]
    for cat in time_cats:
        print(cat.insert())

    ticket_number = 1
    for i in range(TICKET_COUNT):
        ticket = create_ticket(project, users, time_cats, tag_groups, assignment_tags, ticket_number, None)
        ticket_number += 1

        for j in range(subticket_count()):
            subticket = create_ticket(project, users, time_cats, tag_groups, assignment_tags, ticket_number, ticket)
            ticket_number += 1

    print("commit;")
    print("vacuum full freeze analyze;")

if __name__ == "__main__":
    main()
