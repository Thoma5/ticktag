export function using<T>(getKey: (obj: T) => any): (a: T, b: T) => number {
    return (a: T, b: T) => {
        let aVal = getKey(a);
        let bVal = getKey(b);
        if (aVal < bVal) {
            return -1;
        } else if (aVal > bVal) {
            return 1;
        } else {
            return 0;
        }
    };
}
