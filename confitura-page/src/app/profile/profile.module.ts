import {NgModule} from '@angular/core';
import {SharedModule} from '../shared/shared.module';
import {ProfileEditComponent} from './profile-edit/profile-edit.component';
import {ProfileViewComponent} from './profile-view/profile-view.component';
import {PresentationEditComponent} from './presentation-edit/presentation-edit.component';
import {TagInputModule} from 'ngx-chips';
import {ProfileCompleteGuard} from './shared/profile-complete-guard.service';
import {IsAuthenticatedGuard} from './shared/is-authenticated-guard.service';
import {SpeakerSelectComponent} from './speaker-select/speaker.multiselect.component';
import {CospeakersComponent} from './cospeakers/cospeakers.component';
import {ParticipationStatusComponent} from './participation-status/participation-status.component';
import {ProfileRoutingModule} from './profile.routing';


@NgModule({
  imports: [
    ProfileRoutingModule,
    SharedModule,
    TagInputModule,
  ],
  declarations: [
    ProfileEditComponent,
    ProfileViewComponent,
    PresentationEditComponent,
    SpeakerSelectComponent,
    CospeakersComponent,
    ParticipationStatusComponent
  ],
  providers: [ProfileCompleteGuard, IsAuthenticatedGuard]

})
export class ProfileModule {

}
