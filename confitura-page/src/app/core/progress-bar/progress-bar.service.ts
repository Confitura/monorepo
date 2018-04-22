import {Injectable} from '@angular/core';

@Injectable()
export class ProgressBarService {
  counter: number;

  constructor() {
    this.counter = 0;
  }

  shouldShow() {
    return this.counter > 0;
  }

  started() {
    this.counter++;
    console.log('started', this.counter);
  }

  done() {
    if (this.counter > 0) {
      this.counter--;
    }
    console.log('done ', this.counter);

  }
}
