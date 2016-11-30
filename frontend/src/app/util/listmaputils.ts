import * as imm from 'immutable';

export function idListToMap<T extends {id: string}>(list: T[]): imm.Map<string, T> {
  let result: {[id: string]: T} = {};
  for (let item of list) {
    result[item.id] = item;
  }
  return imm.Map(result);
}
