import * as imm from 'immutable';

// Note: To avoid issues with the evaluation order, each regular expression is
// surrounded with `(?:` and `)`. This means you can treat variable interpolation
// as a single token in the regular expressions.
function r(s: string): string {
  return String.raw`(?:${s})`;
}

export const SEPERATOR_FRONT_REGEX = r(String.raw`[^\w\d]|^`);
export const SEPERATOR_BACK_REGEX = r(String.raw`[^\w\d]|$`);
export const TAG_LETTER = r(String.raw`[a-z0-9_]`);
export const USER_LETTER = r(String.raw`[a-z0-9_]`);

export const TIME_REGEX = r(String.raw`(?:[0-9]+h?:?[0-9]+(?:m(?:in)?)?)|(?:[0-9]+(?:h|(?:m(?:in)?))?)`);
export const TAG_REGEX = r(String.raw`${TAG_LETTER}+`);
export const USER_REGEX = r(String.raw`${USER_LETTER}+`);

export const TIME_CMD_REGEX = r(String.raw`time:${TIME_REGEX}@${TAG_REGEX}`);
export const ASSIGN_CMD_REGEX = r(String.raw`assign:${USER_REGEX}@${TAG_REGEX}`);
export const UNASSIGN_CMD_REGEX = r(String.raw`-assign:${USER_REGEX}(?:@${TAG_REGEX}?)?`);
export const CLOSE_CMD_REGEX = r(String.raw`close`);
export const REOPEN_CMD_REGEX = r(String.raw`reopen`);
export const TAG_CMD_REGEX = r(String.raw`-?tag:${TAG_REGEX}`);
export const EST_CMD_REGEX = r(String.raw`est:${TIME_REGEX}`);
export const SP_CMD_REGEX = r(String.raw`sp:[0-9]+`);
export const DUE_CMD_REGEX = r(String.raw`due:[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}`);

export const COMMAND_REGEX = r(
  String.raw`!(?:` +
  String.raw`${TIME_CMD_REGEX}|${ASSIGN_CMD_REGEX}|${UNASSIGN_CMD_REGEX}|` +
  String.raw`${CLOSE_CMD_REGEX}|${REOPEN_CMD_REGEX}|${TAG_CMD_REGEX}|` +
  String.raw`${EST_CMD_REGEX}|${SP_CMD_REGEX}|${DUE_CMD_REGEX}` +
  String.raw`)`
);
export const TICKET_REF_REGEX = r(String.raw`#[0-9]+`);
export const EXPR_REGEX = r(String.raw`${SEPERATOR_FRONT_REGEX}(${COMMAND_REGEX}|${TICKET_REF_REGEX})(?=${SEPERATOR_BACK_REGEX})`);

export const COMMAND_STRINGS = ['time', 'assign', '-assign', 'close', 'reopen', 'tag', '-tag', 'est', 'sp', 'due'];

// TypeScript discriminated union over the `cmd` field.
export type TimeCmd = {
  cmd: 'time',
  minutes: number,
  category: string,
};
export type AssignCmd = {
  cmd: 'assign',
  user: string,
  tag: string,
};
export type UnassignCmd = {
  cmd: 'unassign',
  user: string,
  tag: string|null,
};
export type CloseCmd = {
  cmd: 'close',
};
export type ReopenCmd = {
  cmd: 'reopen',
};
export type TagCmd = {
  cmd: 'tag',
  tag: string,
};
export type UntagCmd = {
  cmd: 'untag',
  tag: string,
};
export type EstCmd = {
  cmd: 'est',
  minutes: number,
};
export type SpCmd = {
  cmd: 'sp',
  number: number,
};
export type DueCmd = {
  cmd: 'due',
  date: number,
};
export type RefTicketCmd = {
  cmd: 'refTicket',
  ticket: number,
};
export type Cmd = TimeCmd | AssignCmd | UnassignCmd | CloseCmd | ReopenCmd | TagCmd | UntagCmd | EstCmd | SpCmd | DueCmd | RefTicketCmd;

export function extractCommands(string: string): imm.List<Cmd> {
  let result = new Array<Cmd>();
  let regex = new RegExp(EXPR_REGEX, 'gimu');
  let match: RegExpExecArray;
  while (match = regex.exec(string)) {
    let command = match[1];
    if (command.startsWith('!time:')) {
      let parts = command.substr(6).split('@');
      result.push({
        cmd: 'time',
        minutes: parseTime(parts[0]),
        category: parts[1],
      });
    } else if (command.startsWith('!assign:')) {
      let split = command.split(/:|@/);
      result.push({
        cmd: 'assign',
        user: split[1],
        tag: split[2],
      });
    } else if (command.startsWith('!-assign:')) {
      let split = command.split(/:|@/);
      result.push({
        cmd: 'unassign',
        user: split[1],
        tag: split[2] || null,
      });
    } else if (command.startsWith('!close')) {
      result.push({ cmd: 'close' });
    } else if (command.startsWith('!reopen')) {
      result.push({ cmd: 'reopen' });
    } else if (command.startsWith('!tag:')) {
      let split = command.split(/:/);
      result.push({
        cmd: 'tag',
        tag: split[1],
      });
    } else if (command.startsWith('!-tag:')) {
      let split = command.split(/:/);
      result.push({
        cmd: 'untag',
        tag: split[1],
      });
    } else if (command.startsWith('!est:')) {
      let time = command.substr(5);
      result.push({
        cmd: 'est',
        minutes: parseTime(time),
      });
    } else if (command.startsWith('!sp:')) {
      let sp = parseInt(command.substr(4), 10);
      result.push({
        cmd: 'sp',
        number: sp,
      });
    } else if (command.startsWith('!due:')) {
      let [year, month, day] = command.substr(5).split('-').map(s => parseInt(s, 10));
      let date = Date.UTC(year, month - 1, day);
      result.push({
        cmd: 'due',
        date: date,
      });
    } else if (command[0] === '#') {
      result.push({
        cmd: 'refTicket',
        ticket: parseInt(command.substr(1), 10),
      });
    } else {
      throw new Error('This cannot happen aka the regex is full of bugs!');
    }
  }
  return imm.List(result);
}

// Makes time to minutes
export function parseTime(string: string): number {
  let numbers = /^(\d+)(?:\D+(\d+))?/.exec(string);
  if (numbers[1] && numbers[2]) {
    // Two numbers always means hours then minutes.
    return parseInt(numbers[1], 10) * 60 + parseInt(numbers[2], 10);
  } else {
    // One number depends on the suffix of the string
    if (string.endsWith('min') || string.endsWith('m')) {
      return parseInt(numbers[1], 10);
    } else {
      return parseInt(numbers[1], 10) * 60;
    }
  }
}

export function htmlifyCommands(string: string, projectId: string): string {
  let commandRegex = new RegExp(r(
    String.raw`(${SEPERATOR_FRONT_REGEX})(${COMMAND_REGEX})(?=${SEPERATOR_BACK_REGEX})`
  ), 'gimu');
  string = string.replace(commandRegex, '$1<span style="font-size:75%; color: gray;">$2</span>');

  let ticketRefRegex = new RegExp(r(
    String.raw`(${SEPERATOR_FRONT_REGEX})#([0-9]+)(?=${SEPERATOR_BACK_REGEX})`
  ), 'gimu');
  string = string.replace(
    ticketRefRegex,
    `$1<a
      href="/project/${projectId}/ticket/$2"
      data-projectId="${projectId}"
      data-ticketNumber="$2"
      class="grammar-htmlifyCommands">#$2</a>`
  );

  return string;
}
