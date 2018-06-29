import {Directive, HostListener, Input} from '@angular/core';
import {MatDialog} from '@angular/material';
import {RatePresentationDialogComponent} from './rate-presentation-dialog.component';
import {RatingService} from '../rating.service';

@Directive({
  selector: '[cfRatePresentationDialog]'
})
export class RatePresentationDialogDirective {

  @Input() presentationId;

  constructor(public dialog: MatDialog, private service: RatingService) {
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
        this.service.save(result, this.presentationId).subscribe();
      }
    });
  }

}
