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
import { ResizeImgPipe } from './resize-img.pipe';
import {MdPipe} from './md/md.pipe';
import { PageHeaderComponent } from './page-header/page-header.component';
import {BackButtonComponent} from './back-button/back-button.component';
import {MatButtonModule, MatIconModule} from '@angular/material';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    FileUploadModule,
    FormsModule,
    MatIconModule,
    MatButtonModule
  ],
  declarations: [
    AdminDirective,
    AdminOrOwnerDirective,
    PhotoComponent,
    SocialLinksComponent,
    LazySrcDirective,
    PageComponent,
    PersonModalComponent,
    PersonComponent,
    PersonViewComponent,
    ResizeImgPipe,
    MdPipe,
    PageHeaderComponent,
    BackButtonComponent,
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
    PersonViewComponent,
    ResizeImgPipe,
    MdPipe,
    PageHeaderComponent,
    BackButtonComponent
  ],
  providers: [PersonModalService]
})
export class SharedModule {

}
