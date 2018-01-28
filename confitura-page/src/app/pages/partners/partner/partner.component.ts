import {PartnerService} from '../shared/partner.service';
import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Partner} from '../shared/partner.model';
import {FileUploader} from 'ng2-file-upload';
import {CurrentUser} from '../../../core/security/current-user.service';
import * as Marked from 'marked';
import {Location} from '@angular/common';
import {environment} from '../../../../environments/environment';
import {switchMap} from 'rxjs/operators';

@Component({
  templateUrl: './partner.component.html',
  styleUrls: ['partner.component.scss']
})
export class PartnerComponent {
  partner: Partner;
  uploader: FileUploader;

  constructor(private route: ActivatedRoute,
              private service: PartnerService,
              private currentUser: CurrentUser,
              private location: Location) {
    this.uploader = new FileUploader({
      authToken: this.currentUser.getToken(),
      autoUpload: true
    });
    this.uploader.onCompleteAll = () => {
      this.service.getBy(this.partner.id)
        .subscribe((partner) => {
          this.partner = partner;
          this.partner.logo += `?v=${new Date().getMilliseconds()}`;
        });

    };


    this.route.params
      .pipe(
        switchMap((params) => this.service.getBy(params['id']))
      )
      .subscribe((partner) => {
        this.partner = partner;
        $(window).scrollTop(0);
        this.uploader.options.url = `${environment.API_URL}/resources/partners/${partner.id}`;
      });

  }


  uploadLogo() {
    $('input[type=file]').click();
  }

  delete() {
    this.service.delete(this.partner)
      .subscribe(() => this.location.back());
  }
}
