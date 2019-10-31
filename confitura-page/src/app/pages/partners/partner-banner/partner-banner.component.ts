import {Component, OnInit} from '@angular/core';
import {Partner} from '../shared/partner.model';
import {PartnerService} from '../shared/partner.service';

@Component({
  selector: 'cf-partners-banner',
  templateUrl: './partner-banner.component.html',
  styleUrls: ['./partner-banner.component.scss']

})
export class PartnerBannerComponent implements OnInit {

  types = ['platinum', 'gold', 'big data & AI', 'silver'];
  list: Partner[] = [];

  constructor(private service: PartnerService) {
  }

  ngOnInit(): void {
    this.service.getAll()
      .subscribe((partners) => {
        this.list = partners;
        setTimeout(() =>
          $('.bxslider')
            .bxSlider({
              mode: 'fade',
              auto: true,
              controls: false,
              preloadImages: 'all',
              pager: false,
              randomStart: true,
              autoHover: true
            }));
      });

  }

  hasAnyPartnersFor(type: string): boolean {
    return this.getPartnersFor(type).length > 0;
  }

  getPartnersFor(type: string): Partner[] {
    return this.list
      .filter((partner: Partner) => partner.type === type);
  }

  getSimpleType(type) {
    return type.split(' ')[0];
  }
}
