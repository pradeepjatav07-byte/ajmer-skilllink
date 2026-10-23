export const categories = [
  { id: 'electrical', name: 'Electrical', sub: 'Wiring, lights & more', icon: 'bolt', tone: 'amber' },
  { id: 'plumbing', name: 'Plumbing', sub: 'Leaks, taps & fittings', icon: 'droplet', tone: 'blue' },
  { id: 'appliance', name: 'Appliance repair', sub: 'AC, fridge & washing machine', icon: 'fan', tone: 'violet' },
  { id: 'cleaning', name: 'Home cleaning', sub: 'A fresh start, every time', icon: 'sparkles', tone: 'mint' },
  { id: 'carpentry', name: 'Carpentry', sub: 'Furniture & woodwork', icon: 'hammer', tone: 'orange' },
  { id: 'computer', name: 'Computer repair', sub: 'Laptop, desktop & Wi-Fi', icon: 'laptop', tone: 'indigo' },
  { id: 'painting', name: 'Painting', sub: 'Walls, finishes & polish', icon: 'paint', tone: 'pink' },
  { id: 'mechanic', name: 'Mechanic', sub: 'Two-wheeler & car service', icon: 'car', tone: 'slate' },
  { id: 'tutoring', name: 'Tutoring', sub: 'Personalized learning', icon: 'book', tone: 'cyan' },
  { id: 'tailoring', name: 'Tailoring', sub: 'Alterations & custom fit', icon: 'scissors', tone: 'rose' },
  { id: 'photography', name: 'Photography', sub: 'Events & portraits', icon: 'camera', tone: 'purple' },
];

const providerSeeds = [
  ['p01','Aarav Sharma','Electrical','Civil Lines','AS',4.9,186,8,450,900,96,'Verified',14,true,false,'Available today'],
  ['p02','Imran Khan','Plumbing','Vaishali Nagar','IK',4.8,142,7,350,750,94,'Verified',18,true,false,'Available today'],
  ['p03','Meera Joshi','Home cleaning','Panchsheel Nagar','MJ',4.9,208,6,600,1200,98,'Verified',11,false,true,'Available tomorrow'],
  ['p04','Vikram Singh','Appliance repair','Ana Sagar','VS',4.7,119,9,500,1100,91,'Verified',22,true,false,'Available today'],
  ['p05','Nisha Rathore','Tutoring','Civil Lines','NR',4.9,96,5,400,800,95,'Verified',16,false,true,'Available today'],
  ['p06','Rafiq Ahmed','Carpentry','Naya Bazaar','RA',4.8,158,11,550,1250,93,'Verified',19,true,false,'Available tomorrow'],
  ['p07','Kunal Mehta','Computer repair','Beawar Road','KM',4.7,87,6,350,900,89,'Verified',25,false,false,'Available today'],
  ['p08','Pooja Saini','Painting','Madar Gate','PS',4.8,74,8,700,1500,92,'Verified',17,false,true,'Available this week'],
  ['p09','Deepak Choudhary','Mechanic','Vaishali Nagar','DC',4.6,203,10,400,950,88,'Verified',24,true,false,'Available today'],
  ['p10','Sana Qureshi','Tailoring','Naya Bazaar','SQ',4.9,129,7,250,700,97,'Verified',12,false,true,'Available today'],
  ['p11','Mahesh Kumawat','Electrical','Panchsheel Nagar','MK',4.6,92,12,400,850,86,'Under review',31,true,false,'Available today'],
  ['p12','Ritu Bhandari','Home cleaning','Ana Sagar','RB',4.8,103,4,550,1100,91,'Verified',20,false,true,'Available tomorrow'],
  ['p13','Arjun Pareek','Plumbing','Madar Gate','AP',4.5,68,5,300,700,82,'Under review',34,true,false,'Available today'],
  ['p14','Farhan Ali','Appliance repair','Naya Bazaar','FA',4.8,154,9,450,1050,95,'Verified',15,true,false,'Available today'],
  ['p15','Kavita Yadav','Photography','Civil Lines','KY',4.9,62,8,1500,3500,94,'Verified',18,false,true,'Available this week'],
  ['p16','Ramesh Jangid','Carpentry','Beawar Road','RJ',4.7,111,13,500,1400,89,'Verified',26,false,false,'Available tomorrow'],
  ['p17','Neha Sharma','Computer repair','Vaishali Nagar','NS',4.8,83,5,400,950,93,'Verified',16,false,true,'Available today'],
  ['p18','Suresh Prajapat','Painting','Panchsheel Nagar','SP',4.6,97,14,650,1600,87,'Verified',27,false,false,'Available today'],
  ['p19','Anil Bairwa','Mechanic','Madar Gate','AB',4.5,172,12,350,850,84,'Under review',36,true,false,'Available today'],
  ['p20','Shweta Jain','Tutoring','Ana Sagar','SJ',4.9,117,8,450,900,96,'Verified',13,false,true,'Available today'],
  ['p21','Mohammad Salim','Electrical','Naya Bazaar','MS',4.7,134,16,450,1000,91,'Verified',21,true,false,'Available today'],
  ['p22','Kartik Solanki','Plumbing','Beawar Road','KS',4.6,59,4,300,650,83,'Verified',29,true,false,'Available tomorrow'],
  ['p23','Lata Kanwar','Tailoring','Panchsheel Nagar','LK',4.8,145,10,250,650,95,'Verified',17,false,true,'Available today'],
  ['p24','Yash Vyas','Appliance repair','Civil Lines','YV',4.7,102,7,500,1150,90,'Verified',23,true,false,'Available today'],
  ['p25','Gopal Lal','Home cleaning','Vaishali Nagar','GL',4.6,77,6,500,1000,86,'Under review',32,false,false,'Available this week'],
];

export const seedProviders = providerSeeds.map((p, i) => ({
  id: p[0], name: p[1], category: p[2], area: p[3], initials: p[4], rating: p[5], jobs: p[6],
  experience: p[7], min: p[8], max: p[9], trust: p[10], verification: p[11], response: p[12],
  emergency: p[13], female: p[14], availability: p[15], onTime: Math.min(99, 82 + (p[10] % 17)),
  cancellation: Math.max(1, 12 - Math.round((p[10] - 80) / 3)), languages: i % 3 === 0 ? ['Hindi', 'English'] : ['Hindi'],
  skills: [p[2], i % 2 === 0 ? 'Preventive maintenance' : 'Installation & repair', i % 3 === 0 ? 'Emergency visits' : 'Home service'],
  about: `${p[1].split(' ')[0]} is a ${p[7]}-year local professional serving ${p[3]} and nearby Ajmer. Known for clear estimates, careful workmanship, and friendly service.`,
  color: ['blue','violet','teal','orange','rose','indigo'][i % 6],
  portfolio: ['Recent home service', 'Careful finish', 'Customer approved work'],
  reviewText: ['Arrived on time and explained the options clearly.', 'Good work, fair estimate, and very courteous.', 'A reliable local professional. Would book again.'][i % 3],
}));

const activeSeed = [
  { id:'ASL-2408', providerId:'p01', category:'Electrical', service:'Ceiling fan installation', area:'Civil Lines', status:'Requested', date:'Today', time:'4:30 PM', customer:'Rohan Mehta', emergency:false },
  { id:'ASL-2407', providerId:'p02', category:'Plumbing', service:'Kitchen tap leakage', area:'Vaishali Nagar', status:'Accepted', date:'Today', time:'5:00 PM', customer:'Priya Sharma', emergency:false },
  { id:'ASL-2406', providerId:'p04', category:'Appliance repair', service:'AC not cooling', area:'Ana Sagar', status:'Provider on the way', date:'Today', time:'3:45 PM', customer:'Rohan Mehta', emergency:true },
  { id:'ASL-2405', providerId:'p03', category:'Home cleaning', service:'Deep home cleaning', area:'Panchsheel Nagar', status:'Work started', date:'Tomorrow', time:'10:00 AM', customer:'Neha Joshi', emergency:false },
  { id:'ASL-2404', providerId:'p06', category:'Carpentry', service:'Wardrobe hinge repair', area:'Naya Bazaar', status:'Requested', date:'Tomorrow', time:'11:30 AM', customer:'Amit Verma', emergency:false },
  { id:'ASL-2403', providerId:'p07', category:'Computer repair', service:'Laptop diagnostics', area:'Beawar Road', status:'Accepted', date:'18 Oct', time:'12:00 PM', customer:'Rohan Mehta', emergency:false },
  { id:'ASL-2402', providerId:'p09', category:'Mechanic', service:'Scooter servicing', area:'Madar Gate', status:'Work completed', date:'Yesterday', time:'2:00 PM', customer:'Isha Jain', emergency:false },
  { id:'ASL-2401', providerId:'p11', category:'Electrical', service:'Switchboard inspection', area:'Civil Lines', status:'Requested', date:'18 Oct', time:'9:30 AM', customer:'Aarav Saini', emergency:false },
];
const completedSeed = Array.from({ length: 15 }, (_, i) => {
  const provider = seedProviders[(i * 3 + 1) % seedProviders.length];
  return {
    id: `ASL-${2301 + i}`, providerId: provider.id, category: provider.category,
    service: [`Routine service & inspection`, `Fixture repair`, `Installation and maintenance`][i % 3],
    area: provider.area, status: 'Customer confirmed', date: `${(i % 14) + 1} Oct`, time: '11:00 AM',
    customer: ['Rohan Mehta', 'Priya Sharma', 'Neha Joshi', 'Amit Verma'][i % 4], emergency: false,
    amount: provider.min + 150, reviewed: i < 5,
  };
});

export const seedBookings = [...activeSeed, ...completedSeed];

export const seedCustomers = [
  ['c01','Rohan Mehta','customer@ajmerskilllink.demo','Civil Lines','RM'],
  ['c02','Priya Sharma','priya.sharma@ajmerskilllink.demo','Vaishali Nagar','PS'],
  ['c03','Neha Joshi','neha.joshi@ajmerskilllink.demo','Panchsheel Nagar','NJ'],
  ['c04','Amit Verma','amit.verma@ajmerskilllink.demo','Ana Sagar','AV'],
  ['c05','Aarav Saini','aarav.saini@ajmerskilllink.demo','Naya Bazaar','AS'],
  ['c06','Isha Jain','isha.jain@ajmerskilllink.demo','Beawar Road','IJ'],
  ['c07','Kabir Rathore','kabir.rathore@ajmerskilllink.demo','Madar Gate','KR'],
  ['c08','Tara Kumari','tara.kumari@ajmerskilllink.demo','Civil Lines','TK'],
  ['c09','Dev Bhandari','dev.bhandari@ajmerskilllink.demo','Ana Sagar','DB'],
  ['c10','Zoya Khan','zoya.khan@ajmerskilllink.demo','Panchsheel Nagar','ZK'],
  ['c11','Nikhil Pareek','nikhil.pareek@ajmerskilllink.demo','Vaishali Nagar','NP'],
  ['c12','Kiran Yadav','kiran.yadav@ajmerskilllink.demo','Naya Bazaar','KY'],
].map(([id,name,email,area,initials]) => ({ id,name,email,area,initials,status:'Active',joined:'Sep 2026' }));

export const seedReviews = seedProviders.slice(0, 8).map((p, i) => ({
  id: `REV-${410 + i}`, providerId: p.id, provider: p.name, customer: ['Rohan Mehta','Priya Sharma','Neha Joshi','Amit Verma'][i % 4],
  rating: [5,5,4,5,4,5,5,4][i], text: p.reviewText, date: `${15 - i} Oct 2026`, reported: i === 6,
}));

export const seedNotifications = [
  { id:'n1', title:'Your provider is on the way', body:'Vikram Singh is heading to your home for AC repair.', time:'12 min ago', unread:true, kind:'booking' },
  { id:'n2', title:'New service request', body:'A customer requested electrical work in Civil Lines.', time:'38 min ago', unread:true, kind:'request' },
  { id:'n3', title:'Profile verified', body:'Your Skill Passport verification is complete.', time:'Yesterday', unread:false, kind:'trust' },
];

export const demoStats = { customers: 1248, providers: 186, pendingVerifications: 4, activeBookings: 27, emergencyRequests: 3, openComplaints: 5, completedJobs: 1842, rating: 4.86 };
