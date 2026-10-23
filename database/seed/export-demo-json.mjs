import { categories, seedBookings, seedCustomers, seedProviders, seedReviews } from '../../frontend/src/data.js';
import { mkdir, writeFile } from 'node:fs/promises';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
const root=resolve(dirname(fileURLToPath(import.meta.url)),'../..');
const uid=(g,n)=>`00000000-0000-4000-${g}-${String(n).padStart(12,'0')}`;
const customerIds=new Map(seedCustomers.map((c,i)=>[c.name,uid('8001',101+i)]));
const providerIds=new Map(seedProviders.map((p,i)=>[p.id,uid('8002',201+i)]));
const providerNameToId=new Map(seedProviders.map((p,i)=>[p.name,uid('8002',201+i)]));
const serviceIds=new Map(categories.map((c,i)=>[c.name,uid('8003',i+1)]));
const bookingIds=new Map(seedBookings.map((b,i)=>[b.id,uid('8004',i+1)]));
const demoUsers=[{id:uid('8000',1),name:'Nexoraa Admin',email:'admin@ajmerskilllink.demo',role:'ADMIN',area:'Ajmer, Rajasthan',status:'ACTIVE'}];
seedCustomers.forEach((c,i)=>demoUsers.push({id:uid('8001',101+i),name:c.name,email:c.name==='Rohan Mehta'?'customer@ajmerskilllink.demo':c.email,role:'CUSTOMER',area:c.area,status:'ACTIVE'}));
seedProviders.forEach((p,i)=>demoUsers.push({id:uid('8002',201+i),name:p.name,email:p.id==='p01'?'provider@ajmerskilllink.demo':`provider.${p.id}@ajmerskilllink.demo`,role:'PROVIDER',area:p.area,status:'ACTIVE'}));
const services=categories.map((c,i)=>({id:serviceIds.get(c.name),name:c.name,slug:c.id,description:c.sub,active:true,indicativeRanges:seedProviders.filter(p=>p.category===c.name).map(p=>({min:p.min,max:p.max})).slice(0,3)}));
const providers=seedProviders.map(p=>({id:providerIds.get(p.id),userId:providerIds.get(p.id),providerCode:`ASL-${p.id.toUpperCase()}`,name:p.name,categoryId:serviceIds.get(p.category),category:p.category,area:p.area,experienceYears:p.experience,skills:p.skills,about:p.about,verificationStatus:p.verification.toUpperCase().replaceAll(' ','_'),ratingAverage:p.rating,completedJobs:p.jobs,trustScore:p.trust,onTimeRate:p.onTime,cancellationRate:p.cancellation,emergencyAvailable:p.emergency,femaleProfessional:p.female,availableToday:p.availability.includes('today'),availabilityLabel:p.availability,estimatedRange:{min:p.min,max:p.max},fictional:true}));
const bookingStatuses={'Requested':'REQUESTED','Accepted':'ACCEPTED','Provider on the way':'PROVIDER_ON_THE_WAY','Work started':'WORK_STARTED','Work completed':'WORK_COMPLETED','Customer confirmed':'CUSTOMER_CONFIRMED','Cancelled':'CANCELLED','Disputed':'DISPUTED'};
const bookings=seedBookings.map((b,i)=>({id:bookingIds.get(b.id),bookingCode:b.id,customerId:customerIds.get(b.customer)||customerIds.get('Rohan Mehta'),providerId:providerIds.get(b.providerId)||null,categoryId:serviceIds.get(b.category),category:b.category,serviceTitle:b.service,area:b.area,addressLabel:`Fictional demo address, ${b.area}, Ajmer`,appointmentDate:b.date,preferredTime:b.time,status:bookingStatuses[b.status],emergency:b.emergency,estimatedRange:{min:seedProviders.find(p=>p.id===b.providerId)?.min||400,max:seedProviders.find(p=>p.id===b.providerId)?.max||900},fictional:true}));
const completed=seedBookings.filter(b=>b.status==='Customer confirmed');
const reviews=seedReviews.slice(0,completed.length).map((r,i)=>({id:uid('8013',i+1),bookingId:bookingIds.get(completed[i].id),providerId:providerIds.get(completed[i].providerId),customerId:customerIds.get(completed[i].customer)||customerIds.get('Rohan Mehta'),rating:r.rating,reviewText:r.text,reported:r.reported,createdAt:r.date,fictional:true}));
const complaintSeed=[
 ['CMP-501','Service arrived later than expected','Priya Sharma','p09','Late arrival / no-show','Provider arrived later than the agreed demo window. Customer requested an updated arrival time.','OPEN','NORMAL'],
 ['CMP-502','Estimate changed after inspection','Amit Verma','p04','Price or estimate','Customer requested a written explanation of parts and labour after inspection.','UNDER_REVIEW','HIGH'],
 ['CMP-503','Work proof needs clarification','Neha Joshi','p06','Work proof','Customer asked support to clarify a completion photo in the demo record.','RESOLVED','NORMAL'],
 ['CMP-504','Provider did not arrive','Isha Jain','p13','Late arrival / no-show','Customer reports no arrival update and requests follow-up.','OPEN','HIGH'],
 ['CMP-505','Request to review a rating','Kabir Rathore','p07','Reported review','A demo review was flagged for moderation review.','UNDER_REVIEW','NORMAL'],
];
const complaints=complaintSeed.map((c,i)=>({id:uid('8014',i+1),complaintCode:c[0],title:c[1],customerId:customerIds.get(c[2]),providerId:providerIds.get(c[3]),category:c[4],description:c[5],status:c[6],priority:c[7],bookingId:bookingIds.get(completed[i]?.id)||null,createdAt:`2026-10-${String(15-i).padStart(2,'0')}`,fictional:true}));
const fairQuotes=categories.map((c,i)=>{const p=seedProviders.find(x=>x.category===c.name);return{id:uid('8006',i+1),categoryId:serviceIds.get(c.name),category:c.name,area:'Ajmer - all areas',estimatedMin:p?.min||350,estimatedMax:p?.max||900,visitFeeMin:80,visitFeeMax:180,emergencySurchargeMin:120,emergencySurchargeMax:280,disclaimer:'Indicative range only; final price may change after inspection.',fictional:true}});
const files={'users.json':demoUsers,'providers.json':providers,'services.json':services,'bookings.json':bookings,'reviews.json':reviews,'complaints.json':complaints,'fair-quote-rules.json':fairQuotes};
const out=resolve(root,'data-export');await mkdir(out,{recursive:true});
for(const [name,value] of Object.entries(files))await writeFile(resolve(out,name),JSON.stringify(value,null,2)+'\n','utf8');
console.log(`Exported ${demoUsers.length} user records, ${providers.length} providers, ${bookings.length} bookings, ${reviews.length} reviews, ${complaints.length} complaints and ${fairQuotes.length} FairQuote rules.`);
