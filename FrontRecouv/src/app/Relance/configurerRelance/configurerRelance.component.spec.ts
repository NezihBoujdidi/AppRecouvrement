import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigurerRelanceComponent } from './configurerRelance.component';

describe('RelanceComponent', () => {
  let component: ConfigurerRelanceComponent;
  let fixture: ComponentFixture<ConfigurerRelanceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfigurerRelanceComponent]
    });
    fixture = TestBed.createComponent(ConfigurerRelanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
