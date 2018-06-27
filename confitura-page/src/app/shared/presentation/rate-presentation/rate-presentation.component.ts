import {Component, Input, OnInit} from '@angular/core';
import {RatingService} from './rating.service';
import {Rate} from './rating.model';
import {CurrentUser} from '../../../core/security/current-user.service';

@Component({
  selector: 'cf-rate-presentation',
  templateUrl: './rate-presentation.component.html',
  styleUrls: ['./rate-presentation.component.css']
})
export class RatePresentationComponent implements OnInit {

  @Input()
  presentationId: string;

  showCommentField = false;

  rate: Rate = new Rate();
  numericValue = -1;
  displayValue = '';

  error = '';

  authenticated: boolean;

  formatter: (value: number | null) => string | number = value => rates[value].description;

  constructor(private service: RatingService, currentUser: CurrentUser) {
    this.authenticated = currentUser.isAvailable();
  }

  ngOnInit() {
    const rate = this.service.getRate(this.presentationId);
    if (rate) {
      this.numericValue = this.getRateValueByName(rate.value).no;
      this.displayValue = this.getRateValueByName(rate.value).description;
      this.rate = rate;
    }
  }

  setValue(v) {
    const rate = this.getByNo(v.value);
    this.rate.value = rate.name;
    this.service.save(this.rate, this.presentationId)
      .subscribe(() => {
        this.displayValue = rate.description;
        this.showComment();
      }, err => {
        if (err.status === 409) {
          this.error = 'Sorry, but it looks like you already rated this presentation on different device';
        } else if (err.status === 403) {
          this.error = 'According to our system you haven\'t passed registration yet, so there is no way you have seen this presentation!';
        } else {
          this.error = 'Something funny happened. Can you try again later? Or just let as know. Error log should be in console';
          console.log('promised error log in console', err);
        }
      });

  }

  hideComment() {
    this.showCommentField = false;
  }

  showComment() {
    this.showCommentField = true;
  }

  addComment(c) {
    this.service.addComment(c, this.presentationId).subscribe(() => this.hideComment());
  }

  private getRateValueByName(value: string) {
    return rates.find(it => it.name === value);
  }

  private getByNo(v) {
    return rates.find(it => it.no === v);
  }
}

const rates = [
  {no: 1, name: 'TERRIBLE', description: 'Terrible'},
  {no: 2, name: 'BAD', description: 'Bad'},
  {no: 3, name: 'IT_WAS_FINE', description: 'It was fine'},
  {no: 4, name: 'GREAT', description: 'Great'},
  {no: 5, name: 'AWESOME', description: 'Awesome!!!'}
];
