import { categories, seedBookings, seedCustomers, seedProviders, seedReviews } from '../../frontend/src/data.js';
import { mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..');
const hash = '$2b$10$8Z73/jCwjxHCb5VNIfC.r.kvYOakM3ch3yB4MajQyVrnSnUpbSzKW'; // SkillLink@123; dev-only BCrypt
const q = v => v === null || v === undefined ? 'NULL' : `'${String(v).replaceAll('\\', '\\\\').replaceAll("'", "''")}'`;
const n = v => Number.isFinite(Number(v)) ? String(Number(v)) : '0';
const b = v => v ? '1' : '0';
const uid = (group, num) => `00000000-0000-4000-${group}-${String(num).padStart(12, '0')}`;
const userIds = new Map();
const categoryIds = new Map(categories.map((c,i)=>[c.name,uid('8003',i+1)]));
const providerById = new Map(seedProviders.map(p=>[p.id,p]));
const customerByName = new Map(seedCustomers.map((c,i)=>[c.name,{...c,dbId:uid('8001',101+i)}]));
const demoEmails = new Map([['Rohan Mehta','customer@ajmerskilllink.demo'],['Aarav Sharma','provider@ajmerskilllink.demo']]);
const slugMap = new Map(categories.map(c=>[c.name,c.id]));
const rows = [];
rows.push('-- Fictional demo seed only. Development password for all accounts: SkillLink@123. Change before production.');
rows.push(`INSERT INTO service_categories (id,name,slug,description,active,created_at) VALUES\n${categories.map((c,i)=>`(${q(categoryIds.get(c.name))},${q(c.name)},${q(c.id)},${q(c.sub)},1,'2026-10-01 09:00:00')`).join(',\n')}\nON DUPLICATE KEY UPDATE name=VALUES(name),description=VALUES(description),active=1;`);

const adminId = uid('8000',1);
const users = [{id:adminId,name:'Nexoraa Admin',email:'admin@ajmerskilllink.demo',role:'ADMIN',status:'ACTIVE',area:'Ajmer, Rajasthan'}];
for (const [index,c0] of seedCustomers.entries()) {
  const c = {...c0};
  if (c.name==='Rohan Mehta') c.email='customer@ajmerskilllink.demo';
  const id = uid('8001',101+index); userIds.set(c.name,id);
  users.push({id,name:c.name,email:c.email,role:'CUSTOMER',status:'ACTIVE',area:c.area});
}
for (const [index,p] of seedProviders.entries()) {
  const id=uid('8002',201+index);userIds.set(p.id,id);userIds.set(p.name,id);
  const email=p.id==='p01'?'provider@ajmerskilllink.demo':`provider.${p.id}@ajmerskilllink.demo`;
  users.push({id,name:p.name,email,role:'PROVIDER',status:'ACTIVE',area:p.area});
}
rows.push(`INSERT INTO users (id,full_name,email,password_hash,role,status,created_at,updated_at) VALUES\n${users.map(u=>`(${q(u.id)},${q(u.name)},${q(u.email)},${q(hash)},${q(u.role)},${q(u.status)},'2026-10-01 09:00:00','2026-10-01 09:00:00')`).join(',\n')}\nON DUPLICATE KEY UPDATE full_name=VALUES(full_name),password_hash=VALUES(password_hash),role=VALUES(role),status='ACTIVE';`);
rows.push(`INSERT INTO customer_profiles (user_id,preferred_language,profile_complete,created_at) VALUES\n${users.filter(u=>u.role==='CUSTOMER').map(u=>`(${q(u.id)},'English',1,'2026-10-01 09:00:00')`).join(',\n')}\nON DUPLICATE KEY UPDATE preferred_language=VALUES(preferred_language);`);

const areas=['Civil Lines','Vaishali Nagar','Panchsheel Nagar','Ana Sagar','Naya Bazaar','Madar Gate','Beawar Road'];
rows.push(`INSERT INTO provider_profiles (user_id,primary_category_id,provider_code,primary_area,experience_years,skills_json,about_text,verification_status,rating_average,completed_jobs,trust_score,on_time_rate,cancellation_rate,emergency_available,female_professional,available_today,active,created_at,updated_at) VALUES\n${seedProviders.map(p=>{
  const id=userIds.get(p.id), status=p.verification==='Verified'?'VERIFIED':p.verification.toUpperCase().replaceAll(' ','_');
  return `(${q(id)},${q(categoryIds.get(p.category))},${q(`ASL-${p.id.toUpperCase()}`)},${q(p.area)},${n(p.experience)},${q(JSON.stringify(p.skills))},${q(p.about)},${q(status)},${n(p.rating)},${n(p.jobs)},${n(p.trust)},${n(p.onTime)},${n(p.cancellation)},${b(p.emergency)},${b(p.female)},${b(p.availability.includes('today'))},1,'2026-10-01 09:00:00','2026-10-15 09:00:00')`;
}).join(',\n')}\nON DUPLICATE KEY UPDATE primary_category_id=VALUES(primary_category_id),verification_status=VALUES(verification_status),trust_score=VALUES(trust_score),active=1;`);

const providerServices=[]; const serviceAreas=[]; const availability=[];
seedProviders.forEach((p,i)=>{
  const pid=userIds.get(p.id), categoryId=categoryIds.get(p.category);
  p.skills.forEach((skill,j)=>providerServices.push(`(${q(uid('8007',i*4+j+1))},${q(pid)},${q(categoryId)},${q(skill)},1,'2026-10-01 09:00:00')`));
  const nextArea=areas[(areas.indexOf(p.area)+1)%areas.length];
  [p.area,nextArea].forEach((area,j)=>serviceAreas.push(`(${q(uid('8008',i*2+j+1))},${q(pid)},${q(area)},${b(j===0)})`));
  availability.push(`(${q(uid('8009',i+1))},${q(pid)},${i%7+1},'09:00:00','18:00:00',${b(p.emergency)})`);
});
rows.push(`INSERT INTO provider_services (id,provider_id,category_id,skill_name,active,created_at) VALUES\n${providerServices.join(',\n')}\nON DUPLICATE KEY UPDATE skill_name=VALUES(skill_name);`);
rows.push(`INSERT INTO service_areas (id,provider_id,area_name,is_primary) VALUES\n${serviceAreas.join(',\n')}\nON DUPLICATE KEY UPDATE is_primary=VALUES(is_primary);`);
rows.push(`INSERT INTO provider_availability (id,provider_id,day_of_week,start_time,end_time,emergency_available) VALUES\n${availability.join(',\n')}\nON DUPLICATE KEY UPDATE emergency_available=VALUES(emergency_available);`);
rows.push(`INSERT INTO addresses (id,user_id,label,area_name,address_line,is_default,created_at) VALUES\n${seedCustomers.map((c,i)=>`(${q(uid('8010',i+1))},${q(userIds.get(c.name))},'Home',${q(c.area)},${q(`Fictional demo address, ${c.area}, Ajmer`)},1,'2026-10-01 09:00:00')`).join(',\n')}\nON DUPLICATE KEY UPDATE area_name=VALUES(area_name);`);
rows.push(`INSERT INTO fair_quote_rules (id,category_id,area_name,estimated_min,estimated_max,visit_fee_min,visit_fee_max,emergency_surcharge_min,emergency_surcharge_max,active,updated_at) VALUES\n${categories.map((c,i)=>{
  const p=seedProviders.find(x=>x.category===c.name); const min=p?.min??350; const max=p?.max??900;
  return `(${q(uid('8006',i+1))},${q(categoryIds.get(c.name))},'Ajmer - all areas',${min},${max},80,180,120,280,1,'2026-10-12 10:00:00')`;
}).join(',\n')}\nON DUPLICATE KEY UPDATE estimated_min=VALUES(estimated_min),estimated_max=VALUES(estimated_max),active=1;`);

const bookingIdMap = new Map();
const statusDb = new Map([['Requested','REQUESTED'],['Accepted','ACCEPTED'],['Provider on the way','PROVIDER_ON_THE_WAY'],['Work started','WORK_STARTED'],['Work completed','WORK_COMPLETED'],['Customer confirmed','CUSTOMER_CONFIRMED'],['Cancelled','CANCELLED'],['Disputed','DISPUTED']]);
function dateFor(value, index) {
  if (value==='Today') return '2026-10-15'; if (value==='Tomorrow') return '2026-10-16'; if (value==='Yesterday') return '2026-10-14';
  const m=String(value).match(/(\d+)\s+Oct/i); if(m) return `2026-10-${String(Number(m[1])).padStart(2,'0')}`;
  return `2026-10-${String(15-index%14).padStart(2,'0')}`;
}
const bookingRows=seedBookings.map((b0,i)=>{
  const bid=uid('8004',i+1);bookingIdMap.set(b0.id,bid);
  const provider=providerById.get(b0.providerId); const customer=userIds.get(b0.customer)||userIds.get('Rohan Mehta');
  return {src:b0,id:bid,customer,provider:userIds.get(b0.providerId)||null,category:categoryIds.get(b0.category),providerData:provider};
});
rows.push(`INSERT INTO bookings (id,booking_code,customer_id,provider_id,category_id,service_title,problem_description,area_name,address_label,appointment_date,preferred_time,status,is_emergency,estimated_min,estimated_max,verified_provider_only,daytime_preferred,female_professional_preferred,trusted_contact_name,share_with_contact,created_at,updated_at) VALUES\n${bookingRows.map((x,i)=>{
 const z=x.src; const p=x.providerData;
 return `(${q(x.id)},${q(z.id)},${q(x.customer)},${q(x.provider)},${q(x.category)},${q(z.service)},${q(`Fictional demo service request: ${z.service.toLowerCase()}.`)},${q(z.area)},${q(`Fictional demo address, ${z.area}, Ajmer`)},${z.date==='Today'&&z.emergency?'NULL':q(dateFor(z.date,i))},${q(z.time)},${q(statusDb.get(z.status)||'REQUESTED')},${b(z.emergency)},${n(p?.min??400)},${n(p?.max??900)},1,1,0,NULL,0,'2026-10-15 09:00:00','2026-10-15 09:00:00')`;
}).join(',\n')}\nON DUPLICATE KEY UPDATE status=VALUES(status),updated_at=VALUES(updated_at);`);

const historyRows=[]; let h=1;
const progression=['REQUESTED','ACCEPTED','PROVIDER_ON_THE_WAY','WORK_STARTED','WORK_COMPLETED','CUSTOMER_CONFIRMED'];
bookingRows.forEach((x,i)=>{
  const current=statusDb.get(x.src.status)||'REQUESTED';
  let sequence=progression.slice(0,Math.max(1,progression.indexOf(current)+1));
  if(current==='CANCELLED') sequence=['REQUESTED','CANCELLED'];
  if(current==='DISPUTED') sequence=['REQUESTED','ACCEPTED','WORK_STARTED','DISPUTED'];
  sequence.forEach((status,j)=>historyRows.push(`(${q(uid('8011',h++))},${q(x.id)},${q(j===0?x.customer:x.provider||x.customer)},${q(status)},${q(j===0?'Customer request submitted':'Fictional demo status update')},'2026-10-${String(Math.min(15,j+1)).padStart(2,'0')} 10:00:00')`));
});
rows.push(`INSERT INTO booking_status_history (id,booking_id,changed_by,status_value,note,created_at) VALUES\n${historyRows.join(',\n')};`);
const attachments=[];
bookingRows.filter(x=>['Customer confirmed','Work completed'].includes(x.src.status)).slice(0,5).forEach((x,i)=>{
  attachments.push(`(${q(uid('8012',i*2+1))},${q(x.id)},${q(x.provider||x.customer)},'BEFORE',${q(`demo-before-${i+1}.jpg`)},${q(`demo:work-proof-before-${i+1}`)},'2026-10-14 09:15:00',1,'2026-10-14 09:15:00')`);
  attachments.push(`(${q(uid('8012',i*2+2))},${q(x.id)},${q(x.provider||x.customer)},'AFTER',${q(`demo-after-${i+1}.jpg`)},${q(`demo:work-proof-after-${i+1}`)},'2026-10-14 11:45:00',1,'2026-10-14 11:45:00')`);
});
rows.push(`INSERT INTO attachments (id,booking_id,uploaded_by,phase,file_name,demo_url,captured_at,consent_confirmed,created_at) VALUES\n${attachments.join(',\n')};`);
const completed=bookingRows.filter(x=>x.src.status==='Customer confirmed');
rows.push(`INSERT INTO ratings_reviews (id,booking_id,customer_id,provider_id,rating,review_text,reported,created_at) VALUES\n${seedReviews.slice(0,Math.min(seedReviews.length,completed.length)).map((r,i)=>{
 const x=completed[i]; return `(${q(uid('8013',i+1))},${q(x.id)},${q(x.customer)},${q(x.provider)},${r.rating},${q(r.text)},${b(r.reported)},'2026-10-${String(15-i).padStart(2,'0')} 12:00:00')`;
}).join(',\n')};`);
const complaintExamples=[
 ['Service arrived later than expected','Priya Sharma','Deepak Choudhary','Late arrival / no-show','Provider arrived later than the agreed demo window. Customer requested an updated arrival time.','OPEN','NORMAL'],
 ['Estimate changed after inspection','Amit Verma','Vikram Singh','Price or estimate','Customer requested a written explanation of parts and labour after inspection.','UNDER_REVIEW','HIGH'],
 ['Work proof needs clarification','Neha Joshi','Rafiq Ahmed','Work proof','Customer asked support to clarify a completion photo in the demo record.','RESOLVED','NORMAL'],
 ['Provider did not arrive','Isha Jain','Arjun Pareek','Late arrival / no-show','Customer reports no arrival update and requests follow-up.','OPEN','HIGH'],
 ['Request to review a rating','Kabir Rathore','Kunal Mehta','Reported review','A demo review was flagged for moderation review.','UNDER_REVIEW','NORMAL'],
];
rows.push(`INSERT INTO complaints (id,booking_id,customer_id,provider_id,category,description,status,priority,created_at,updated_at) VALUES\n${complaintExamples.map((c,i)=>{
 const x=bookingRows[i+8]; return `(${q(uid('8014',i+1))},${q(x?.id)},${q(userIds.get(c[1])||x?.customer)},${q(userIds.get(seedProviders.find(p=>p.name===c[2])?.id)||x?.provider)},${q(c[3])},${q(c[4])},${q(c[5])},${q(c[6])},'2026-10-${String(15-i).padStart(2,'0')} 12:00:00','2026-10-${String(15-i).padStart(2,'0')} 12:00:00')`;
}).join(',\n')};`);
rows.push(`INSERT INTO trusted_contacts (id,customer_id,display_name,contact_hint,consent_confirmed,created_at) VALUES (${q(uid('8015',1))},${q(userIds.get('Rohan Mehta'))},'Demo trusted contact','No contact details stored',0,'2026-10-01 09:00:00');`);
rows.push(`INSERT INTO notifications (id,user_id,title,body,kind,read_at,created_at) VALUES\n(${q(uid('8016',1))},${q(userIds.get('Rohan Mehta'))},'Your provider is on the way','Vikram Singh is heading to your home for AC repair.','booking',NULL,'2026-10-15 11:48:00'),\n(${q(uid('8016',2))},${q(userIds.get('Aarav Sharma'))},'New service request','A customer requested electrical work in Civil Lines.','request',NULL,'2026-10-15 11:22:00'),\n(${q(uid('8016',3))},${q(adminId)},'Verification queue updated','Four fictional provider profiles are awaiting review.','trust',NULL,'2026-10-15 09:10:00');`);
rows.push(`INSERT INTO audit_logs (id,actor_id,action_key,target_type,target_id,details,created_at) VALUES (${q(uid('8017',1))},${q(adminId)},'DEMO_SEED_LOADED','PLATFORM','ajmer-skilllink','Fictional records loaded for product demonstration.','2026-10-15 09:00:00');`);
rows.push(`INSERT INTO admin_announcements (id,title,body,audience,published,starts_at,ends_at,created_by,created_at) VALUES (${q(uid('8018',1))},'Welcome to Ajmer SkillLink','Keep your profile and availability up to date. Demo announcement only.','ALL',1,'2026-10-15 00:00:00','2026-10-31 23:59:59',${q(adminId)},'2026-10-15 09:00:00');`);

const sql = rows.join('\n\n') + '\n';
const targets=[resolve(root,'backend/src/main/resources/db/migration/V2__demo_seed.sql'),resolve(root,'database/seed/demo-data.sql')];
for (const file of targets) { await mkdir(dirname(file),{recursive:true}); await writeFile(file,sql,'utf8'); }
console.log(`Wrote ${targets.length} seed SQL copies (${users.length} users, ${seedProviders.length} providers, ${seedCustomers.length} core customers, ${seedBookings.length} bookings).`);
