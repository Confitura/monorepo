import {Directive, HostListener, Input} from '@angular/core';
import {MatDialog, MatSnackBar} from '@angular/material';
import {RatePresentationDialogComponent} from './rate-presentation-dialog.component';
import {RatingService} from '../rating.service';

@Directive({
  selector: '[cfRatePresentationDialog]'
})
export class RatePresentationDialogDirective {

  @Input() presentationId;

  constructor(public dialog: MatDialog, private service: RatingService, public snackBar: MatSnackBar) {
  }

  @HostListener('click')
  onClick() {
    console.log('click');
    const dialogRef = this.dialog.open(RatePresentationDialogComponent, {
      data: {
        presentationId: this.presentationId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.save(result, this.presentationId).subscribe(
          () => this.success(),
          err => this.error(err)
        );
      }
    });
  }

  private success() {
    this.snackBar.open('rate saved', null, {
      duration: 2000
    });
  }

  private error(err: any) {
    let error = '';
    if (err.status === 409) {
      error = 'Sorry, but it looks like you already rated this presentation on different device';
    } else if (err.status === 403) {
      error = 'According to our system you haven\'t passed registration yet, so there is no way you have seen this presentation!';
    } else {
      error = 'Something funny happened. Can you try again later? Or just let as know. Error log should be in console';
      console.log('promised error log in console', err);
    }

    this.snackBar.open(error, null, {
      duration: 5000
    });
  }
}
