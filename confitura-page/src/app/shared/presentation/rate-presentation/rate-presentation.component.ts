import {Component, Input, OnInit} from '@angular/core';
import {RatingService} from './rating.service';
import {Rate} from './rating.model';
import {CurrentUser} from '../../../core/security/current-user.service';
import {RatePresentationDialogComponent} from './rate-presentation-dialog/rate-presentation-dialog.component';
import {MatDialog} from '@angular/material';


@Component({
  selector: 'cf-rate-presentation',
  templateUrl: './rate-presentation.component.html',
  styleUrls: ['./rate-presentation.component.css']
})
export class RatePresentationComponent implements OnInit {

  @Input()
  presentationId: string;
  rate: Rate = new Rate();

  error = '';

  authenticated: boolean;

  formatter: (value: number | null) => string | number = value => this.service.getRateMetaByIndex(value).description;

  constructor(private service: RatingService, currentUser: CurrentUser, public dialog: MatDialog) {
    this.authenticated = currentUser.isAvailable();
  }

  ngOnInit() {
    const rate = this.service.getRate(this.presentationId);
    if (rate) {
      this.rate = rate;
    }
  }

  updateRate(rate: Rate) {
    this.rate = rate;
    console.log(rate);
    this.service.save(this.rate, this.presentationId)
      .subscribe(() => {
        this.showComment();
      }, err => {
        this.handleError(err);
      });
  }

  private handleError(err) {
    if (err.status === 409) {
      this.error = 'Sorry, but it looks like you already rated this presentation on different device';
    } else if (err.status === 403) {
      this.error = 'According to our system you haven\'t passed registration yet, so there is no way you have seen this presentation!';
    } else {
      this.error = 'Something funny happened. Can you try again later? Or just let as know. Error log should be in console';
      console.log('promised error log in console', err);
    }
  }

  showComment() {
    this.openDialog();
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(RatePresentationDialogComponent, {
      data: {
        rate: this.rate,
        presentationId: this.presentationId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      if (result) {
        this.rate = result;
        this.service.save(this.rate, this.presentationId)
          .subscribe(() => {
          }, err => {
            this.handleError(err);
          });
      }
    });
  }


}
