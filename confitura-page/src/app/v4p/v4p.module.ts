import {NgModule} from '@angular/core';
import {V4pRoutingModule} from './v4p-routing.module';
import {SharedModule} from '../shared/shared.module';
import {V4pComponent} from './v4p.component';
import {V4pService} from './v4p.service';
import {V4pStartComponent} from './start/v4p-start.component';
import {V4pEndComponent} from './end/v4p-end.component';
import {MatButtonModule, MatIconModule} from '@angular/material';

import {HotkeyModule} from 'angular2-hotkeys';

@NgModule({
  imports: [
    SharedModule,
    V4pRoutingModule,
    MatIconModule,
    MatButtonModule,
    HotkeyModule,
  ],
  declarations: [
    V4pComponent,
    V4pStartComponent,
    V4pEndComponent
  ],
  providers: [V4pService]
})
export class V4pModule {

}
