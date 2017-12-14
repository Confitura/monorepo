import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AdminDirective} from './admin.directive';
import {PhotoComponent} from './photo/photo.component';
import {SocialLinksComponent} from './social-links/social-links.component';
import {HttpClientModule} from '@angular/common/http';
import {LazySrcDirective} from './lazy-src.directive';
import {PageComponent} from './page/page.component';
import {PersonModalComponent} from './person-modal/person-modal.component';
import {PersonComponent} from './person/person.component';
import {PersonViewComponent} from './person-view/person-view.component';
import {FileUploadModule} from 'ng2-file-upload';
import {PersonModalService} from './person-modal/person-modal.service';
import {AdminOrOwnerDirective} from './admin-or-owner.directive';

@NgModule({
  imports: [CommonModule, HttpClientModule, FileUploadModule],
  declarations: [
    AdminDirective,
    AdminOrOwnerDirective,
    PhotoComponent,
    SocialLinksComponent,
    LazySrcDirective,
    PageComponent,
    PersonModalComponent,
    PersonComponent,
    PersonViewComponent
  ],
  exports: [
    CommonModule,
    FormsModule,
    RouterModule,
    AdminDirective,
    AdminOrOwnerDirective,
    PhotoComponent,
    SocialLinksComponent,
    LazySrcDirective,
    PageComponent,
    PersonModalComponent,
    PersonComponent,
    PersonViewComponent
  ],
  providers: [PersonModalService]
})
export class SharedModule {

}
