import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoirRelancesComponent } from './voir-relances.component';

describe('VoirRelancesComponent', () => {
  let component: VoirRelancesComponent;
  let fixture: ComponentFixture<VoirRelancesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VoirRelancesComponent]
    });
    fixture = TestBed.createComponent(VoirRelancesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
