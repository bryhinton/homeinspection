<!DOCTYPE html>
<html manifest="cachefiles.appcache">
	<head>
		<title>Home Inspection</title>
		<script type="text/javascript" src="scripts/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="scripts/database.js"></script>
		<script type="text/javascript" src="scripts/inspection.js"></script>
		<script type="text/javascript" src="scripts/utils/utils.js"></script>
		<!--[if lt IE 9]>
		<script type="text/javascript" src="scripts/flashcanvas.js"></script>
		<![endif]-->
		<script src="scripts/jSignature.min.js"></script>
		<link rel="stylesheet" href="stylesheets/main.css">
		<link rel="icon" type="image/png"  href="favicon.ico">
		<link rel="apple-touch-icon-precomposed" href="favicon.ico"/>
		<meta name='viewport' content='width=device-width, minimum-scale=1.0, maximum-scale=1.0' >
		<meta name="apple-mobile-web-app-capable" content="yes" />
	</head>
    <body onload='setTimeout(function() { window.scrollTo(0, 1) }, 100);'>
		<div id="loading">
			<div class="background"></div>
			<div class="loading-text">Loading...</div>
			<button type="button" onclick="showResetDialog();" class="reset-button blue-gradient">Reset</button>
		</div>
		<div id="message">
			<div class="loading-text"></div>
		</div>
		<div id="delete-inspection-dialog" class="dialog">
			<span class="info-i">i</span>
			<div class="confirm-text">
				Are you sure you want to delete this inspection?
			</div>
			<div class="dialog-submit dialog-button" onclick="toggleMask(); deleteInspectionAndReload(null, false);">Yes</div>
			<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
		</div>
		<div id="logout-dialog" class="dialog">
			<span class="info-i">i</span>
			<div class="confirm-text">
				Are you sure you want to logout? This will delete this inspection.
			</div>
			<div class="dialog-submit dialog-button" onclick="deleteInspectionAndReload(null, true);">Yes</div>
			<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
		</div>
		<div id="reset-dialog" class="dialog">
			<span class="info-i">i</span>
			<div class="confirm-text">
				Are you sure you want to reset this device? This cannot be undone.
			</div>
			<div class="dialog-submit dialog-button" onclick="$('#reset-dialog').css('display', 'none'); logout();">Yes</div>
			<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
		</div>
		<div id="unfinished-inspection-dialog" class="dialog">
			<span class="info-i">i</span>
			<div class="confirm-text">
				You have an unfinished inspection. Would you like to continue?
			</div>
			<input type="hidden" id="unfinished-id">
			<input type="hidden" id="handle-unfinished-inspection">
			<div class="dialog-submit dialog-button" onclick="loadInspection();">Yes</div>
			<div class="dialog-cancel dialog-button" onclick="sendUnfinishedInspection()">Start New</div>
		</div>
		<div id="login" class="light-blue-gradient-background">
			<input type="hidden" id="company-id" name="companyID" value="">
			<input type="hidden" id="tech-id" name="techID" value="">

			<div class='title'>
				<div class="title-text">Home Safety Inspection</div>
			</div>

			<div class="area-name">Login</div>
			<div class="gap"></div>

			<div class="line-item">
				<div class='label'>Username</div>
				<div class='value'><input type="text" id="username" autocapitalize="off"></div>
			</div>
			<div class="line-item">
				<div class='label'>Password</div>
				<div class='value'><input type="password" id="password"></div>
			</div>
			<div class="line-item">
				<div class='label'></div>
				<div class='value'><button type="button" class="table-button" onclick="login()">Login</button></div>
			</div>
			<div class="line-item">
				<div class="label"></div>
				<div class="value login-message"></div>
			</div>
			<button type="button" onclick="showResetDialog();" class="reset-button login blue-gradient">Reset</button>
		</div>
		<div id="customer-info" class="light-blue-gradient-background">
			<div class='title'>
				<div class="title-text">Home Safety Inspection</div>
			</div>

			<div class="area-name">Start New Inspection</div>
			<div class="gap"></div>

			<div class="line-item">
				<div class='label'>First Name</div>
				<div class='value'><input type="text" id="first-name"></div>
			</div>
			<div class="line-item">
				<div class='label'>Last Name</div>
				<div class='value'><input type="text" id="last-name"></div>
			</div>
			<div class="line-item">
				<div class='label'>Address</div>
				<div class='value'><input type="text" id="address"></div>
			</div>
			<div class="line-item">
				<div class='label'>City</div>
				<div class='value'><input type="text" id="city"></div>
			</div>
			<div class="line-item">
				<div class='label'>State</div>
				<div class='value'><input type="text" id="state"></div>
			</div>
			<div class="line-item">
				<div class='label'>ZIP Code</div>
				<div class='value'><input type="text" id="zip-code"></div>
			</div>
			<div class="line-item">
				<div class='label'>Phone Number</div>
				<div class='value'><input type="text" id="phone-number"></div>
			</div>
			<div class="line-item">
				<div class='label'>Email</div>
				<div class='value'><input type="text" id="email" autocapitalize="off"></div>
			</div>
			<div class="line-item">
				<div class='label'>Technician</div>
				<div class='value' id="tech-first-name"></div>
			</div>
			<div class="line-item">
				<div class='label'></div>
				<div class='value'><button type="button" id="start-inspection" class="table-button" onclick="validateCustomerInfo()">Start Inspection</button></div>
			</div>
		</div>
		<div id="inspection">
			<input type="hidden" id="inspection-id">
			<div id="add-area-dialog" class="dialog">
				<div class="add-area-title">
					Area Name
				</div>
				<input type="text" class="dialog-text" id="new-area-name">
				<input type="hidden" id="new-area-id">
				<div class="dialog-submit dialog-button" onclick="addArea();">Add</div>
				<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
			</div>
			<div id="confirm-finish-dialog" class="dialog">
				<span class="info-i">i</span>
				<div class="confirm-text">
					Do you want to finish and review this inspection?
				</div>
				<div class="dialog-submit dialog-button" onclick="reviewInspection();">Yes</div>
				<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
			</div>
			<div id="line-items" class="line-items light-blue-gradient-background">
				<div id="area-name" class="area-name">Select an area by tapping the Add button</div>
				<div id="line-item-list" class="line-item-list">

				</div>
			</div>
			<div id="side-panel" class="side-panel">
				<div class='title' onclick="showTitleMenu(event, true);">
					<div class="title-text">Home Safety Inspection</div>
				</div>
				<ul class='title-menu'>
					<li class='online'>Online</li>
					<li class="menu-item" onclick="reviewCustomerInfo();">Review Customer Info</li>
					<li class="menu-item" onclick="showDeleteInspectionDialog();">Delete Inspection</li>
					<li class="menu-item" onclick="showLogoutDialog();">Logout</li>
					<li class="menu-item" onclick="showResetDialog();">Reset Device</li>
				</ul>
				<div class='customer'>
					<div class='name' id="inspection-name">Place Holder</div>
					<div class='date' id="inspection-date">Oct. 12, 2012</div>
				</div>
				<div id="areas" class="areas">
				</div>
				<div class='bottom-buttons'>
					<div class='add-button' onclick="toggleHiddenListElement(event, 'add-list');">
						Add
						<ul id="add-list" class="hidden-list">
						</ul>
					</div>
					<div class='finish-button' onclick="confirmReview()">
						Finish
					</div>
				</div>
			</div>
		</div>
		<div id="review">
			<div id="quote" class="dialog full">
				<div class="sub-mask" id="quote-sub-mask"></div>
				<div id="signature-div" class="dialog sub-dialog">
					<div class="add-area-title">Customer Signature Required</div>
					<div id="signature"></div>
					<div class="dialog-submit dialog-button" onclick="hideSubMask(); saveSignature(); showEmailCustomerDialog();">Confirm</div>
					<div class="dialog-cancel dialog-button" onclick="$('#signature').jSignature('clear'); hideSubMask();">Cancel</div>
				</div>
				<div id="email-dialog" class="dialog sub-dialog">
					<div class="add-area-title">
						Email Address
					</div>
					<input type="text" class="dialog-text" id="add-email-address" autocapitalize="off" onkeypress="enterNewEmail(event)">
					<input type="hidden" id="email-address">
					<div class="info-text"></div>
					<button type="button" class="add-email-button pass" onclick="addEmail()">Add Email</button>
					<div class="existing-emails" id="existing-emails"></div>
					<div class="dialog-submit dialog-button" onclick="addEmail(); hideSubMask(); prepareSendInspection();">Finish</div>
					<div class="dialog-cancel dialog-button" onclick="hideSubMask();">Cancel</div>
				</div>
				<div class="add-area-title">Quote</div>
				<div class="quote-items"></div>
				<div class="totals"></div>
				<div class="dialog-submit dialog-button" onclick="showSignature();">Finish</div>
				<div class="dialog-cancel dialog-button" onclick="toggleMask()">Cancel</div>
			</div>
			<div id="pricing-guide" class="dialog large">
				<div class="sub-mask" id="pricing-sub-mask"></div>
				<div class="dialog sub-dialog large" id="review-lineitem-quote-dialog">
					<div id="review-lineitem-name" class="add-area-title">Review</div>
					<div id="review-lineitem-quote"></div>
					<div class="dialog-submit dialog-button" onclick="setQuoteItemCount(); hideSubMask(); toggleMask();">Finish</div>
					<div class="dialog-cancel dialog-button" onclick="hideSubMask();">Add More</div>
				</div>
				<input type="hidden" id="pricing-id">
				<input type="hidden" id="pricing-area">
				<input type="hidden" id="pricing-lineitem-parent">
				<input type="hidden" id="pricing-lineitem">
				<div class="add-area-title">
					Pricing
				</div>
				<div class="loading">We couldn't load your pricing.</div>
				<div id="pricing-table"></div>
			</div>
			<div id="review-line-items" class="line-items light-blue-gradient-background">
				<div id="review-area-name" class="area-name">Select an area by tapping its name</div>
				<div id="review-line-item-list" class="line-item-list">

				</div>
			</div>
			<div id="review-side-panel" class="side-panel">
				<div class='title' onclick="showTitleMenu(event, false);">
					<div class="title-text">Home Safety Inspection</div>
				</div>
				<ul class='title-menu'>
					<li class='online'>Online</li>
					<li class="menu-item" onclick="reviewCustomerInfo();">Review Customer Info</li>
					<li class="menu-item" onclick="showDeleteInspectionDialog();">Delete Inspection</li>
					<li class="menu-item" onclick="showLogoutDialog();">Logout</li>
					<li class="menu-item" onclick="showResetDialog();">Reset Device</li>
				</ul>
				<div class='customer'>
					<div class='name' id="review-name">Place Holder</div>
					<div class='date' id="review-date">Oct. 12, 2012</div>
				</div>
				<div id="review-areas" class="areas">
				</div>
				<div class='bottom-buttons'>
					<div class='add-button' onclick="$('#unfinished-id').val($('#inspection-id').val()); loadInspection();">
						Back
					</div>
					<div class='finish-button' onclick="showQuote();">
						Finish
					</div>
				</div>
			</div>
		</div>
		<div id="debug"></div>
	</body>
</html>