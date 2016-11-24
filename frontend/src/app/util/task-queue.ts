import { Observable, Subscriber } from 'rxjs';

export class Task {
    constructor(
        public obs: Observable<{}>,
        public subs: Subscriber<{}>,
    ) { }
}

export class TaskQueue {
    private queue: Task[] = [];

    public push(task: Task): void {
        this.queue.push(task);
        if (this.queue.length === 1) {
            this.execNext();
        }
    }


    private execNext(): void {
        if (this.queue.length === 0) {
            return;
        }
        let task = this.queue[0];
        task.obs.subscribe(
            val => task.subs.next(val),
            error => { task.subs.error(error); this.popNext(); },
            () => { task.subs.complete(); this.popNext(); }
        );
    }

    private popNext(): void {
        this.queue.shift();
        this.execNext();
    }
}
