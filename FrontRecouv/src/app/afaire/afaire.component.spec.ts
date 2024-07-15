import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AfaireComponent } from './afaire.component';

describe('AfaireComponent', () => {
  let component: AfaireComponent;
  let fixture: ComponentFixture<AfaireComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AfaireComponent]
    });
    fixture = TestBed.createComponent(AfaireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
