import { Observable } from 'rxjs';

export class TaskQueue {
    private queue = new Array<{
        task: Observable<{}>,
        resolve: (v?: {} | PromiseLike<{}>) => void,
        reject: (reason?: any) => void
    }>();

    public push<T>(task: Observable<T>): Observable<T> {
        return Observable.fromPromise(new Promise<T>((resolve, reject) => {
            this.queue.push({ task: task, resolve: resolve, reject: reject });
            if (this.queue.length === 1) {
                this.execNext();
            }
        }));
    }

    private execNext(): void {
        if (this.queue.length === 0) {
            return;
        }
        let item = this.queue[0];
        item.task.subscribe(
            val => { item.resolve(val); },
            error => { item.reject(error); this.popNext(); },
            () => { this.popNext(); }
        );
    }

    private popNext(): void {
        this.queue.shift();
        this.execNext();
    }
}
