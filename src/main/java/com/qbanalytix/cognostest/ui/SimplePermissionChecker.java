package com.qbanalytix.cognostest.ui;

import com.ganesha.desktop.component.permissionutils.PermissionChecker;
import com.ganesha.desktop.component.permissionutils.PermissionControl;

public class SimplePermissionChecker extends PermissionChecker {

	@Override
	public boolean check(PermissionControl permissionControl)
			{
		return true;
	}
}
