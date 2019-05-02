const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;

const eclipsePath = '/Applications/Eclipse.app';

async function main() {
	const targetPath = path.join(__dirname, 'releng/com.aptana.studio.target/com.aptana.studio.target.target');
	const parser = require('xml2json');
	const xml = fs.readFileSync(targetPath, 'utf8');
	const json = parser.toJson(xml, { object: true });
	const locations = json.target.locations.location;
	// Add plugins/features useful for Eclipse development
	locations.unshift({
		repository: {
			location: 'http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.8.1/N/0.8.1.201704211436/'
		},
		unit: {
			id: 'org.sonatype.tycho.m2e.feature.feature.group',
			version: '0.8.1.201704211436'
		}
	});
	// MUST RUN SERIALLY!
	for (const l of locations) {
		await installUnits(l);
	}
}

async function installUnits(location) {
	return new Promise((resolve, reject) => {
		const units = Array.isArray(location.unit) ? location.unit : [ location.unit ]; // each has 'id', 'version'
		const p2RepoURL = location.repository.location;
		const ius = units.map(unit => `${unit.id}/${unit.version}`).join(',');
		const p = spawn(`${eclipsePath}/Contents/MacOS/eclipse`, [
			'-application', 'org.eclipse.equinox.p2.director',
			'-repository', p2RepoURL,
			'-installIU', ius,
			'-nosplash'
		]);
		p.stderr.on('data', data => console.error(data.toString().trim()));
		p.stdout.on('data', data => console.log(data.toString().trim()));
		p.on('close', code => {
			if (code !== 0) {
				reject(code);
			} else {
				resolve(code);
			}
		});
		p.on('error', err => reject(err));
	});
}

main().then(() => {
	console.log('Installed target plugins/features into Eclipse');
	process.exit(0);
}).catch(err => {
	console.error(err);
	process.exit(1);
});